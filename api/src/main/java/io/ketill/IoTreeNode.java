package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A simple implementation for a tree.
 * <p>
 * Any tree can be added to an existing tree, so long as it does not
 * result in an invalid hierarchy.
 * <p>
 * <b>Visibility:</b> This class is {@code package-private} as it is
 * not meant for use outside implementation of the API.
 *
 * @param <T> the contained type.
 * @see #addChild(IoTreeNode)
 */
final class IoTreeNode<T> implements Iterable<IoTreeNode<T>> {

    private static <T> void verifyHierarchy(@NotNull Set<IoTreeNode<T>> seen,
                                            @NotNull IoTreeNode<T> upmost) {
        seen.add(upmost);
        for (IoTreeNode<T> child : upmost) {
            if (seen.contains(child)) {
                throw new IllegalStateException("circular tree");
            } else if (child.parent != upmost) {
                throw new KetillIoBug("incorrect parent");
            }
            seen.add(child);
            verifyHierarchy(seen, child);
        }
    }

    private final @NotNull T value;
    private final @NotNull List<IoTreeNode<T>> children;
    private @Nullable IoTreeNode<T> parent;
    private @NotNull ReadWriteLock upmostLock;

    /**
     * Constructs a new {@code IoTreeNode}.
     *
     * @param value the contained value.
     * @throws NullPointerException if {@code value} is {@code null}.
     */
    public IoTreeNode(@NotNull T value) {
        this.value = Objects.requireNonNull(value, "value cannot be null");
        this.children = new ArrayList<>();
        this.upmostLock = new ReentrantReadWriteLock();
    }

    /**
     * Returns the value of this node.
     *
     * @return the value of this node.
     */
    public @NotNull T getValue() {
        return this.value;
    }

    /**
     * Returns the parent of this node, if any.
     *
     * @return the parent of this node, {@code null} if none.
     */
    public @Nullable IoTreeNode<T> getParent() {
        upmostLock.readLock().lock();
        try {
            return this.parent;
        } finally {
            upmostLock.readLock().unlock();
        }
    }

    /**
     * Returns the upmost parent of this node.
     * <p>
     * The "upmost node" is the furthest node that could be retrieved
     * by getting the current node's parent (starting here) and repeating
     * until the current node has none.
     * <p>
     * <b>Note:</b> Unlike {@link #getParent()}, this method will never
     * return {@code null}. If this node has no parent, it will consider
     * itself as the upmost node and return itself as such.
     *
     * @return the upmost parent of this node.
     */
    public @NotNull IoTreeNode<T> getUpmost() {
        List<IoTreeNode<T>> seen = new ArrayList<>();
        IoTreeNode<T> upmost = this;

        upmostLock.readLock().lock();
        try {
            while (upmost.parent != null) {
                if (seen.contains(upmost)) {
                    /*
                     * This exception is not documented since it can only
                     * occur in the process of adding a child to the node.
                     * This exception is documented there accordingly.
                     */
                    throw new IllegalStateException("circular tree");
                }
                seen.add(upmost);
                upmost = upmost.parent;
            }
        } finally {
            upmostLock.readLock().unlock();
        }

        seen.clear();
        return upmost;
    }

    /**
     * Returns if this node has any children.
     *
     * @return {@code true} if this node has any children, {@code false}
     * otherwise.
     */
    public boolean hasChildren() {
        upmostLock.readLock().lock();
        try {
            return !children.isEmpty();
        } finally {
            upmostLock.readLock().unlock();
        }
    }

    /**
     * Returns if this node has a given child.
     *
     * @param node the node to check for.
     * @return if {@code node} is a child of this node, {@code false}
     * otherwise.
     */
    public boolean hasChild(@Nullable IoTreeNode<T> node) {
        if (node == null) {
            return false;
        }

        upmostLock.readLock().lock();
        try {
            return children.contains(node);
        } finally {
            upmostLock.readLock().unlock();
        }
    }

    /**
     * Returns the number of children this node has. This does not include
     * the children of children.
     *
     * @return the number of children this node has.
     */
    public int getChildCount() {
        upmostLock.readLock().lock();
        try {
            return children.size();
        } finally {
            upmostLock.readLock().unlock();
        }
    }

    /**
     * Returns the children of this node, if any. This does not include
     * the children of children.
     * <p>
     * <b>Warning:</b> The returned list is <i>unmodifiable.</i>
     *
     * @return the children of this node, may be empty.
     * @see #getChildCount()
     * @see #hasChildren()
     */
    public @NotNull List<IoTreeNode<T>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    private void verifyHierarchy() {
        upmostLock.readLock().lock();
        try {
            IoTreeNode<T> upmost = this.getUpmost();
            verifyHierarchy(new HashSet<>(), upmost);
        } finally {
            upmostLock.readLock().unlock();
        }
    }

    /**
     * Adds a child to this node.
     * <p>
     * If the given node is already a child, this method is a no-op.
     * <p>
     * <b>Warning:</b> Different instances of {@code IoTreeNode} are seen
     * as equal so long as they contain equal values. This means adding
     * an {@code IoTreeNode} will result in an invalid tree if the value
     * it contains is already present.
     *
     * @param node the node to add.
     * @return the added node, to allow chaining.
     * @throws NullPointerException  if {@code node} is {@code null}.
     * @throws IllegalStateException if {@code node} belongs to another tree;
     *                               if adding {@code node} as a child would
     *                               result in an invalid hierarchy.
     * @see #removeChild(IoTreeNode)
     */
    public IoTreeNode<T> addChild(@NotNull IoTreeNode<T> node) {
        Objects.requireNonNull(node, "node cannot be null");

        node.upmostLock.writeLock().lock();
        this.upmostLock.writeLock().lock();
        try {
            if (node.parent == this) {
                return node; /* nothing to do */
            } else if (node.parent != null) {
                throw new IllegalStateException(
                        "node belongs to another tree");
            }

            node.parent = this;
            children.add(node);
            this.verifyHierarchy();
        } catch (RuntimeException e) {
            node.parent = null;
            children.remove(node);
            throw e; /* throw back to caller */
        } finally {
            this.upmostLock.writeLock().unlock();
            node.upmostLock.writeLock().unlock();
        }

        /*
         * Do not modify the node's upmost lock reference until we have
         * successfully added it. Otherwise, synchronization between two
         * different trees could occur and possibly cause a soft lock.
         */
        node.upmostLock = this.upmostLock;

        return node;
    }

    /**
     * Removes a child from this node.
     *
     * @param node the node to remove.
     * @return {@code true} if {@code node} was a child of this tree,
     * {@code false} otherwise.
     * @see #addChild(IoTreeNode)
     */
    public boolean removeChild(@Nullable IoTreeNode<T> node) {
        if (node == null) {
            return false; /* nothing to remove */
        }

        this.upmostLock.writeLock().lock();
        node.upmostLock.writeLock().lock();
        try {
            if (node.parent != this) {
                return false; /* not part of this tree */
            }

            if (!children.remove(node)) {
                throw new KetillIoBug("missing child node");
            }

            node.parent = null;
            node.upmostLock = new ReentrantReadWriteLock();

            return true;
        } finally {
            this.upmostLock.writeLock().unlock();
            node.upmostLock.writeLock().unlock();
        }
    }

    /**
     * Returns an iterator of all the children in this node. This does
     * not include the children of children.
     *
     * @return an iterator of all the children in this node.
     */
    @Override
    public @NotNull Iterator<IoTreeNode<T>> iterator() {
        return children.iterator();
    }

    /**
     * Returns the contained value's hash code.
     * <p>
     * Two different nodes with the same value will always have the same
     * hash code, regardless of their parent or children.
     *
     * @return the contained value's hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns if the value this node contains is equal to the
     * value contained by another node.
     *
     * @param obj the reference object with which to compare.
     * @return if the value this node contains is equal to the
     * value {@code obj} contains (assuming it is an instance of
     * {@code IoTreeNode}) are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof IoTreeNode<?>)) {
            return false;
        }
        IoTreeNode<?> that = (IoTreeNode<?>) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public String toString() {
        /*
         * The parent and children are deliberately excluded here since it
         * could lead to a nasty, unreadable string due to recursion (seeing
         * as parent and children contain other instances of IoTreeNode).
         */
        return IoApi.getStrJoiner(this)
                .add("value=" + value)
                .toString();
    }

}