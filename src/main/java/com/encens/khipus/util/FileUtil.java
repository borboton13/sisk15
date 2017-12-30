package com.encens.khipus.util;

/**
 * Encens S.R.L.
 * File util
 *
 * @author
 * @version $Id: FileUtil.java  01-mar-2010 15:50:19$
 */
public final class FileUtil {

    private FileUtil() {
    }
    /**
     * Validate if the current pathName is a valid file
     */
    public static Boolean isValidFile(String pathName) {
        if (ValidatorUtil.isBlankOrNull(pathName)) {
            return false;
        }
        java.io.File file = new java.io.File(pathName);
        return file.isFile() || file.isDirectory() || file.isHidden();
    }
}
