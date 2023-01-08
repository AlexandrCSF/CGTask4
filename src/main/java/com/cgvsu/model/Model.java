package com.cgvsu.model;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3d;

import java.util.*;

public class Model {

    public ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3d> normals = new ArrayList<Vector3d>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
}
