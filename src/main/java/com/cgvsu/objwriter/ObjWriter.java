package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;

public class ObjWriter {
    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";

    public static ArrayList<String> write(Model mesh) {
        ArrayList<String> listFileContent = new ArrayList<>();

        writeVertices(mesh.vertices, listFileContent);
        listFileContent.add("# " + mesh.vertices.size() + " vertices");

        writeTextureVertices(mesh.textureVertices, listFileContent);
        listFileContent.add("# " + mesh.textureVertices.size() + " texture coordinates");

        writeNormals(mesh.normals, listFileContent);
        listFileContent.add("# " + mesh.normals.size() + " vertices");

        writePolygons(mesh.polygons, listFileContent);
        listFileContent.add("# " + mesh.polygons.size() + " polygons");
        return listFileContent;
    }

    public static void writeVertices(final ArrayList<Vector3f> vertices, ArrayList<String> outListFileContent) {
        for (Vector3f vertex : vertices) {
            outListFileContent.add(OBJ_VERTEX_TOKEN + " " + vertex.getX()
                    + " " + vertex.getY() + " " + vertex.getZ());
        }
    }

    public static void writeTextureVertices(final ArrayList<Vector2f> textureVertices, ArrayList<String> outListFileContent) {
        for (Vector2f textureVertex : textureVertices) {
            outListFileContent.add(OBJ_TEXTURE_TOKEN + " " + textureVertex.getX()
                    + " " + textureVertex.getY());
        }
    }

    public static void writeNormals(final ArrayList<Vector3f> normals, ArrayList<String> outListFileContent) {
        for (Vector3f normal : normals) {
            outListFileContent.add(OBJ_NORMAL_TOKEN + " " + normal.getX()
                    + " " + normal.getY() + " " + normal.getZ());
        }
    }

    public static void writePolygons(final ArrayList<Polygon> polygons, ArrayList<String> outListFileContent) {
        for (int i = 0; i < polygons.size(); i++) {
            writeOnePolygons(polygons,i,outListFileContent);
        }
    }

    public static void writeOnePolygons(final ArrayList<Polygon> polygons, int i,ArrayList<String> outListFileContent) {

        StringBuilder strOnePolygons = new StringBuilder();
        strOnePolygons.append(OBJ_FACE_TOKEN + " ");

        try {
            if (polygons.get(i).getSizePolygonsVertexIndices() <= 2)
                throw new IndexOutOfBoundsException("Less than 3 vertices");
            if (polygons.get(i).getSizePolygonsTextureVertexIndices() == 0 &&
                    polygons.get(i).getSizePolygonsNormalIndices() == 0) {
                for (int j = 0; j < polygons.get(i).getSizePolygonsVertexIndices(); j++) {
                    strOnePolygons.append(polygons.get(i).getVertexIndices().get(j) + 1).append(" ");
                }
            } else if (polygons.get(i).getSizePolygonsTextureVertexIndices() != 0 &&
                    polygons.get(i).getSizePolygonsNormalIndices() == 0) {
                for (int j = 0; j < polygons.get(i).getSizePolygonsTextureVertexIndices(); j++) {
                    strOnePolygons.append(polygons.get(i).getVertexIndices().get(j) + 1).append("/").
                            append(polygons.get(i).getTextureVertexIndices().get(j) + 1).append(" ");
                }
            } else {
                for (int j = 0; j < polygons.get(i).getSizePolygonsTextureVertexIndices(); j++) {
                    strOnePolygons.append(polygons.get(i).getVertexIndices().get(j) + 1).append("/").append(
                            polygons.get(i).getTextureVertexIndices().get(j) + 1).append("/").append(
                            polygons.get(i).getNormalIndices().get(j) + 1).append(" ");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ObjWriterException(e.getMessage());
        }
        outListFileContent.add(String.valueOf(strOnePolygons));
    }
}
