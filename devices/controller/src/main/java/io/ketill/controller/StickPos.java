package io.ketill.controller;

import io.ketill.StateContainer;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Read-only view of an analog stick's state.
 */
public class StickPos extends StateContainer<StickPosZ> implements Vector3fc {

    public final @NotNull PressableState up, down, left, right;

    /**
     * @param internalState the internal state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public StickPos(@NotNull StickPosZ internalState) {
        super(internalState);
        this.up = new ButtonState(internalState.up);
        this.down = new ButtonState(internalState.down);
        this.left = new ButtonState(internalState.left);
        this.right = new ButtonState(internalState.right);
    }

    @Override
    public float x() {
        return internalState.x();
    }

    @Override
    public float y() {
        return internalState.y();
    }

    @Override
    public float z() {
        return internalState.z();
    }

    @Override
    public FloatBuffer get(FloatBuffer buffer) {
        return internalState.get(buffer);
    }

    @Override
    public FloatBuffer get(int index, FloatBuffer buffer) {
        return internalState.get(index, buffer);
    }

    @Override
    public ByteBuffer get(ByteBuffer buffer) {
        return internalState.get(buffer);
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        return internalState.get(index, buffer);
    }

    @Override
    public Vector3fc getToAddress(long address) {
        return internalState.getToAddress(address);
    }

    @Override
    public Vector3f sub(Vector3fc v, Vector3f dest) {
        return internalState.sub(v, dest);
    }

    @Override
    public Vector3f sub(float x, float y, float z, Vector3f dest) {
        return internalState.sub(x, y, z, dest);
    }

    @Override
    public Vector3f add(Vector3fc v, Vector3f dest) {
        return internalState.add(v, dest);
    }

    @Override
    public Vector3f add(float x, float y, float z, Vector3f dest) {
        return internalState.add(x, y, z, dest);
    }

    @Override
    public Vector3f fma(Vector3fc a, Vector3fc b, Vector3f dest) {
        return internalState.fma(a, b, dest);
    }

    @Override
    public Vector3f fma(float a, Vector3fc b, Vector3f dest) {
        return internalState.fma(a, b, dest);
    }

    @Override
    public Vector3f mulAdd(Vector3fc a, Vector3fc b, Vector3f dest) {
        return internalState.mulAdd(a, b, dest);
    }

    @Override
    public Vector3f mulAdd(float a, Vector3fc b, Vector3f dest) {
        return internalState.mulAdd(a, b, dest);
    }

    @Override
    public Vector3f mul(Vector3fc v, Vector3f dest) {
        return internalState.mul(v, dest);
    }

    @Override
    public Vector3f div(Vector3fc v, Vector3f dest) {
        return internalState.div(v, dest);
    }

    @Override
    public Vector3f mulProject(Matrix4fc mat, Vector3f dest) {
        return internalState.mulProject(mat, dest);
    }

    @Override
    public Vector3f mulProject(Matrix4fc mat, float w, Vector3f dest) {
        return internalState.mulProject(mat, w, dest);
    }

    @Override
    public Vector3f mul(Matrix3fc mat, Vector3f dest) {
        return internalState.mul(mat, dest);
    }

    @Override
    public Vector3f mul(Matrix3dc mat, Vector3f dest) {
        return internalState.mul(mat, dest);
    }

    @Override
    public Vector3f mul(Matrix3x2fc mat, Vector3f dest) {
        return internalState.mul(mat, dest);
    }

    @Override
    public Vector3f mulTranspose(Matrix3fc mat, Vector3f dest) {
        return internalState.mulTranspose(mat, dest);
    }

    @Override
    public Vector3f mulPosition(Matrix4fc mat, Vector3f dest) {
        return internalState.mulPosition(mat, dest);
    }

    @Override
    public Vector3f mulPosition(Matrix4x3fc mat, Vector3f dest) {
        return internalState.mulPosition(mat, dest);
    }

    @Override
    public Vector3f mulTransposePosition(Matrix4fc mat, Vector3f dest) {
        return internalState.mulTransposePosition(mat, dest);
    }

    @Override
    public float mulPositionW(Matrix4fc mat, Vector3f dest) {
        return internalState.mulPositionW(mat, dest);
    }

    @Override
    public Vector3f mulDirection(Matrix4dc mat, Vector3f dest) {
        return internalState.mulDirection(mat, dest);
    }

    @Override
    public Vector3f mulDirection(Matrix4fc mat, Vector3f dest) {
        return internalState.mulDirection(mat, dest);
    }

    @Override
    public Vector3f mulDirection(Matrix4x3fc mat, Vector3f dest) {
        return internalState.mulDirection(mat, dest);
    }

    @Override
    public Vector3f mulTransposeDirection(Matrix4fc mat, Vector3f dest) {
        return internalState.mulTransposeDirection(mat, dest);
    }

    @Override
    public Vector3f mul(float scalar, Vector3f dest) {
        return internalState.mul(scalar, dest);
    }

    @Override
    public Vector3f mul(float x, float y, float z, Vector3f dest) {
        return internalState.mul(x, y, z, dest);
    }

    @Override
    public Vector3f div(float scalar, Vector3f dest) {
        return internalState.div(scalar, dest);
    }

    @Override
    public Vector3f div(float x, float y, float z, Vector3f dest) {
        return internalState.div(x, y, z, dest);
    }

    @Override
    public Vector3f rotate(Quaternionfc quat, Vector3f dest) {
        return internalState.rotate(quat, dest);
    }

    @Override
    public Quaternionf rotationTo(Vector3fc toDir, Quaternionf dest) {
        return internalState.rotationTo(toDir, dest);
    }

    @Override
    public Quaternionf rotationTo(float toDirX, float toDirY, float toDirZ,
                                  Quaternionf dest) {
        return internalState.rotationTo(toDirX, toDirY, toDirZ, dest);
    }

    @Override
    public Vector3f rotateAxis(float angle, float aX, float aY, float aZ,
                               Vector3f dest) {
        return internalState.rotateAxis(angle, aX, aY, aZ, dest);
    }

    @Override
    public Vector3f rotateX(float angle, Vector3f dest) {
        return internalState.rotateX(angle, dest);
    }

    @Override
    public Vector3f rotateY(float angle, Vector3f dest) {
        return internalState.rotateY(angle, dest);
    }

    @Override
    public Vector3f rotateZ(float angle, Vector3f dest) {
        return internalState.rotateZ(angle, dest);
    }

    @Override
    public float lengthSquared() {
        return internalState.lengthSquared();
    }

    @Override
    public float length() {
        return internalState.length();
    }

    @Override
    public Vector3f normalize(Vector3f dest) {
        return internalState.normalize(dest);
    }

    @Override
    public Vector3f normalize(float length, Vector3f dest) {
        return internalState.normalize(length, dest);
    }

    @Override
    public Vector3f cross(Vector3fc v, Vector3f dest) {
        return internalState.cross(v, dest);
    }

    @Override
    public Vector3f cross(float x, float y, float z, Vector3f dest) {
        return internalState.cross(x, y, z, dest);
    }

    @Override
    public float distance(Vector3fc v) {
        return internalState.distance(v);
    }

    @Override
    public float distance(float x, float y, float z) {
        return internalState.distance(x, y, z);
    }

    @Override
    public float distanceSquared(Vector3fc v) {
        return internalState.distanceSquared(v);
    }

    @Override
    public float distanceSquared(float x, float y, float z) {
        return internalState.distanceSquared(x, y, z);
    }

    @Override
    public float dot(Vector3fc v) {
        return internalState.dot(v);
    }

    @Override
    public float dot(float x, float y, float z) {
        return internalState.dot(x, y, z);
    }

    @Override
    public float angleCos(Vector3fc v) {
        return internalState.angleCos(v);
    }

    @Override
    public float angle(Vector3fc v) {
        return internalState.angle(v);
    }

    @Override
    public float angleSigned(Vector3fc v, Vector3fc n) {
        return internalState.angleSigned(v, n);
    }

    @Override
    public float angleSigned(float x, float y, float z, float nx, float ny,
                             float nz) {
        return internalState.angleSigned(x, y, z, nx, ny, nz);
    }

    @Override
    public Vector3f min(Vector3fc v, Vector3f dest) {
        return internalState.min(v, dest);
    }

    @Override
    public Vector3f max(Vector3fc v, Vector3f dest) {
        return internalState.min(v, dest);
    }

    @Override
    public Vector3f negate(Vector3f dest) {
        return internalState.negate(dest);
    }

    @Override
    public Vector3f absolute(Vector3f dest) {
        return internalState.absolute(dest);
    }

    @Override
    public Vector3f reflect(Vector3fc normal, Vector3f dest) {
        return internalState.reflect(normal, dest);
    }

    @Override
    public Vector3f reflect(float x, float y, float z, Vector3f dest) {
        return internalState.reflect(x, y, z, dest);
    }

    @Override
    public Vector3f half(Vector3fc other, Vector3f dest) {
        return internalState.half(other, dest);
    }

    @Override
    public Vector3f half(float x, float y, float z, Vector3f dest) {
        return internalState.half(x, y, z, dest);
    }

    @Override
    public Vector3f smoothStep(Vector3fc v, float t, Vector3f dest) {
        return internalState.smoothStep(v, t, dest);
    }

    @Override
    public Vector3f hermite(Vector3fc t0, Vector3fc v1, Vector3fc t1,
                            float t, Vector3f dest) {
        return internalState.hermite(t0, v1, t1, t, dest);
    }

    @Override
    public Vector3f lerp(Vector3fc other, float t, Vector3f dest) {
        return internalState.lerp(other, t, dest);
    }

    @Override
    public float get(int component) {
        return internalState.get(component);
    }

    @Override
    public Vector3i get(int mode, Vector3i dest) {
        return internalState.get(mode, dest);
    }

    @Override
    public Vector3f get(Vector3f dest) {
        return internalState.get(dest);
    }

    @Override
    public Vector3d get(Vector3d dest) {
        return internalState.get(dest);
    }

    @Override
    public int maxComponent() {
        return internalState.maxComponent();
    }

    @Override
    public int minComponent() {
        return internalState.minComponent();
    }

    @Override
    public Vector3f orthogonalize(Vector3fc v, Vector3f dest) {
        return internalState.orthogonalize(v, dest);
    }

    @Override
    public Vector3f orthogonalizeUnit(Vector3fc v, Vector3f dest) {
        return internalState.orthogonalizeUnit(v, dest);
    }

    @Override
    public Vector3f floor(Vector3f dest) {
        return internalState.floor(dest);
    }

    @Override
    public Vector3f ceil(Vector3f dest) {
        return internalState.ceil(dest);
    }

    @Override
    public Vector3f round(Vector3f dest) {
        return internalState.round(dest);
    }

    @Override
    public boolean isFinite() {
        return internalState.isFinite();
    }

    @Override
    public boolean equals(Vector3fc v, float delta) {
        return internalState.equals(v, delta);
    }

    @Override
    public boolean equals(float x, float y, float z) {
        return internalState.equals(x, y, z);
    }

}
