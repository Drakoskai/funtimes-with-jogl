package com.drakos.geom;

import com.drakos.util.GLBuffer;
import static com.drakos.util.GLBuffer.VERTEX;
import com.drakos.util.Semantic;
import static com.drakos.util.Semantic.Attr.NORMAL;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER;
import com.jogamp.opengl.GL4;
import static com.jogamp.opengl.GLProfile.GL2;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Drakos
 */
public class Box {
/*
    public static final int OUTSIDE = 0;
    public static final int INSIDE = 1;

    public static final int COUNTER_CLOCKWISE = 0;
    public static final int CLOCKWISE = 1;

    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;

    private static final float[] coord = new float[3];
    private static final int BOX_INDEX_COUNT = 36;
    private static final int BOX_VERTEX_COUNT = 24;
    private static final float B = 1.0f;
    private int orientation = OUTSIDE;
    private static final int faceCount = 6;
    private static final int DEFAULT_SUBDIVISIONS = 0;

    List<Geometry> meshes = new ArrayList<>();

    private static final float[] boxVertexArray
            = { // right
                B, -B, B, // 0
                B, B, B, // 1
                B, -B, -B, // 2
                B, B, -B, // 3

                // front
                -B, B, B, // 4
                B, B, B, // 5
                -B, -B, B, // 6
                B, -B, B, // 7

                // left
                -B, B, B, // 8
                -B, -B, B, // 9
                -B, B, -B, // 10
                -B, -B, -B, // 11

                // back
                B, B, -B, // 12
                -B, B, -B, // 13
                B, -B, -B, // 14
                -B, -B, -B, // 15

                // top
                B, B, B, // 16
                -B, B, B, // 17
                B, B, -B, // 18
                -B, B, -B, // 19

                // bottom
                -B, -B, B, // 20
                B, -B, B, // 21
                -B, -B, -B, // 22
                B, -B, -B // 23
            };

    private static final int[] boxIndexArray
            = {
                2, 3, 1, // right
                2, 1, 0,
                4, 6, 7, // front
                4, 7, 5,
                8, 10, 11, // left
                8, 11, 9,
                12, 14, 15, // back
                12, 15, 13,
                16, 18, 19, // top
                16, 19, 17,
                20, 22, 23, // bottom
                20, 23, 21,};

    private static final int[] boxFacesIndexArray
            = {
                2, 3, 1, // right
                2, 1, 0,
                0, 2, 3, // front
                0, 3, 1,
                0, 2, 3, // left
                0, 3, 1,
                0, 2, 3, // back
                0, 3, 1,
                0, 2, 3, // top
                0, 3, 1,
                0, 2, 3, // bottom
                0, 3, 1,};

    public void DrawGeometry(GL4 gl, int VBAId) {
        for (int i = 0; i < faceCount; i++) {
            Geometry mesh = this.meshes.get(i);
            int size, glType, stride;
            Buffer vertexBuffer, normalBuffer;

            size = mesh.getSize(VERTEX);
            glType = mesh.getGLType(VERTEX);
            stride = mesh.getStride(VERTEX);
            vertexBuffer = mesh.getBuffer(VERTEX);
            gl.glUseProgram(programName);
            gl.glBindVertexArray(vertexArrayName.get(0));

            gl.glBindBufferBase(GL_UNIFORM_BUFFER, // target
                    Semantic.Uniform.TRANSFORM0, // index 
                    bufferName.get(GLBuffer.TRANSFORM)); // buffer

            gl.glDrawElements(
                    GL_TRIANGLES, // primitive mode
                    mesh.getCount(glType), // element count
                    GL_UNSIGNED_SHORT, // element type
                    0); // element offset
        }
    }

    public void makeGeometry(int subdivisions) {
        float radius = 1.0f;
        Geometry dest;

        for (int index = 0; index < faceCount; index++) {
            // create box in model space
            IndexedBuffer itb = buildBoxFace(index, radius, subdivisions);

            FloatBuffer normalBuffer = Buffers.newDirectFloatBuffer(3 * itb.getVertexCount());
            makeIndexedTriangleBufferNormals(itb, normalBuffer);

            FloatBuffer textureCoordBuffer = Buffers.newDirectFloatBuffer(2 * itb.getVertexCount());
            makeUnitBoxTextureCoordinates(index, textureCoordBuffer, itb.getVertexCount());

            dest = new Geometry();

            dest.setElementData(GL.GL_TRIANGLES, itb.getIndexCount(), itb.getIndices());
            dest.setVertexData(itb.getVertexCount(), itb.getVertices());
            dest.setNormalData(normalBuffer.limit(), normalBuffer);
            dest.setTextureCoordData(textureCoordBuffer.limit(), textureCoordBuffer);

            meshes.add(index, dest);
        }
    }

    public IndexedBuffer buildBoxFace(int face, float radius, int subdivisions) {
        IntBuffer indexBuffer = Buffers.newDirectIntBuffer(BOX_INDEX_COUNT / 6);
        FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(3 * BOX_VERTEX_COUNT / 6);

        // fill subset of index buffer
        int[] subArray = new int[BOX_INDEX_COUNT / 6];
        for (int i = 0; i < BOX_INDEX_COUNT / 6; i++) {
            subArray[i] = boxFacesIndexArray[face * BOX_INDEX_COUNT / 6 + i];
        }
        indexBuffer.put(subArray, 0, BOX_INDEX_COUNT / 6);

        float[] vertexSubset = new float[3 * BOX_VERTEX_COUNT / 6];
        for (int i = 0; i < 3 * BOX_VERTEX_COUNT / 6; i++) {
            vertexSubset[i] = boxVertexArray[face * 3 * BOX_VERTEX_COUNT / 6 + i];
        }
        vertexBuffer.put(vertexSubset, 0, 3 * BOX_VERTEX_COUNT / 6);

        // The static box tessellation is assumed to be viewed from the outside. If the orientation is set to
        // inside, then we must reverse the winding order for each triangle's indices.
        if (this.orientation == INSIDE) {
            for (int index = 0; index < BOX_INDEX_COUNT / 6; index += 3) {
                int tmp = indexBuffer.get(index);
                indexBuffer.put(index, indexBuffer.get(index + 2));
                indexBuffer.put(index + 2, tmp);
            }
        }

        // Start with a tessellated box.
        IndexedBuffer itb = new IndexedBuffer(BOX_INDEX_COUNT / 6, indexBuffer, BOX_VERTEX_COUNT / 6, vertexBuffer);

        // Scale each vertex by the specified radius.
        if (radius != 1) {
            vertexBuffer = itb.getVertices();
            for (int vertex = 0; vertex < itb.getVertexCount(); vertex++) {
                mul3AndSet(vertexBuffer, 3 * vertex, radius);
            }
        }

        itb.getVertices().rewind();
        itb.getIndices().rewind();

        return itb;
    }

    public void makeIndexedTriangleBufferNormals(IndexedBuffer itb, FloatBuffer dest) {
        this.makeIndexedTriangleBufferNormals(0, itb.getIndexCount(), itb.getIndices(), 0, itb.getVertexCount(), itb.getVertices(), dest);
    }

    public void makeIndexedTriangleBufferNormals(int indexPos, int indexCount, IntBuffer indices, int vertexPos, int vertexCount, FloatBuffer vertices, FloatBuffer dest) {
        int index;
        float nsign = (this.orientation == OUTSIDE) ? 1.0f : -1.0f;
        float[] norm = new float[3];
        int[] faceIndices = new int[3];

        // Compute the normal for each face, contributing that normal to each vertex of the face.
        for (int i = 0; i < indexCount; i += 3) {
            faceIndices[0] = indices.get(indexPos + i);
            faceIndices[1] = indices.get(indexPos + i + 1);
            faceIndices[2] = indices.get(indexPos + i + 2);
            // Compute the normal for this face.
            this.facenorm(vertices, faceIndices[0], faceIndices[1], faceIndices[2], norm);
            // Add this face normal to the normal at each vertex.
            for (int v = 0; v < 3; v++) {
                index = 3 * faceIndices[v];
                this.add3AndSet(dest, index, norm, 0);
            }
        }

        // Scale and normalize each vertex normal.
        for (int v = 0; v < vertexCount; v++) {
            index = 3 * (vertexPos + v);
            this.mul3AndSet(dest, index, nsign);
            this.norm3AndSet(dest, index);
        }

        dest.rewind();
    }

    public void makeUnitBoxTextureCoordinates(int index, FloatBuffer texCoords, int vertexCount) {
        for (int i = 0; i < vertexCount; i += 4) {
            // V0 (upper left)
            texCoords.put(2 * i, 0);
            texCoords.put(2 * i + 1, 1);
            // V1 (upper right)
            texCoords.put(2 * i + 2, 1);
            texCoords.put(2 * i + 3, 1);
            // V2 (lower left)
            texCoords.put(2 * i + 4, 0);
            texCoords.put(2 * i + 5, 0);
            // V3 (lower right)
            texCoords.put(2 * i + 6, 1);
            texCoords.put(2 * i + 7, 0);
        }

        texCoords.rewind();
    }

    private void facenorm(FloatBuffer srcVerts, int vertA, int vertB, int vertC, float[] dest) {
        int ia = 3 * vertA;
        int ib = 3 * vertB;
        int ic = 3 * vertC;
        float[] ab = new float[3];
        float[] ac = new float[3];

        this.sub3(srcVerts, ib, srcVerts, ia, ab, 0);
        this.sub3(srcVerts, ic, srcVerts, ia, ac, 0);
        this.cross3(ab, ac, dest);
        this.norm3AndSet(dest, 0);
    }

    private void norm3AndSet(float[] src, int srcPos) {
        float len = src[srcPos]
                * src[srcPos]
                + src[srcPos + 1]
                * src[srcPos + 1]
                + src[srcPos + 2]
                * src[srcPos + 2];
        if (len != 0.0f) {
            len = (float) Math.sqrt(len);
            src[srcPos] /= len;
            src[srcPos + 1] /= len;
            src[srcPos + 2] /= len;
        }
    }

    private void add3AndSet(FloatBuffer a, int aPos, float[] b, int bPos) {
        a.put(aPos, a.get(aPos) + b[bPos]);
        a.put(aPos + 1, a.get(aPos + 1) + b[bPos + 1]);
        a.put(aPos + 2, a.get(aPos + 2) + b[bPos + 2]);
    }

    private void sub3(FloatBuffer a, int aPos, FloatBuffer b, int bPos, float[] dest, int destPos) {
        dest[destPos] = a.get(aPos) - b.get(bPos);
        dest[destPos + 1] = a.get(aPos + 1) - b.get(bPos + 1);
        dest[destPos + 2] = a.get(aPos + 2) - b.get(bPos + 2);
    }

    private void cross3(float[] a, float[] b, float[] dest) {
        dest[0] = a[1] * b[2] - a[2] * b[1];
        dest[1] = a[2] * b[0] - a[0] * b[2];
        dest[2] = a[0] * b[1] - a[1] * b[0];
    }

    private void mul3AndSet(FloatBuffer src, int srcPos, float c) {
        src.put(srcPos, src.get(srcPos) * c);
        src.put(srcPos + 1, src.get(srcPos + 1) * c);
        src.put(srcPos + 2, src.get(srcPos + 2) * c);
    }

    private void norm3AndSet(FloatBuffer src, int srcPos) {
        float len;

        len = src.get(srcPos) * src.get(srcPos)
                + src.get(srcPos + 1) * src.get(srcPos + 1)
                + src.get(srcPos + 2) * src.get(srcPos + 2);
        if (len != 0.0f) {
            len = (float) Math.sqrt(len);
            src.put(srcPos, src.get(srcPos) / len);
            src.put(srcPos + 1, src.get(srcPos + 1) / len);
            src.put(srcPos + 2, src.get(srcPos + 2) / len);
        }
    }*/
}
