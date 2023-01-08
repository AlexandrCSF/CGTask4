package com.cgvsu.objreader;

public class ObjReaderExceptions extends RuntimeException {

    protected ObjReaderExceptions(String message) {
        super(message);
    }
    public static class ObjReaderException extends RuntimeException {
        public ObjReaderException(String errorMessage, int lineInd) {
            super("Error parsing OBJ file on line: " + lineInd + ". " + errorMessage);
        }
    }

    public static class ObjContentException extends ObjReaderExceptions {
        public ObjContentException(String errorMessage) {
            super("Error! Impossible format! " + errorMessage);
        }
    }
}