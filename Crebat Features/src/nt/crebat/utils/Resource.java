/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat.utils;

import java.io.File;
import java.io.IOException;
import nt.ps.lang.PSObject;
import nt.ps.pson.PSON;
import nt.ps.pson.PSONException;
import nt.ps.pson.PSONUserdataReader;

/**
 *
 * @author Asus
 */
public final class Resource
{
    private static final File ROOT = new File(System.getProperty("user.dir") + "/data");
    
    private final File base;
    private final String absolutePath;
    
    private Resource(String folder)
    {
        base = new File(ROOT.getAbsolutePath() + "/" + folder);
        base.mkdirs();
        absolutePath = base.getAbsolutePath();
    }
    
    public final File getFile(String path) throws IOException
    {
        File file = new File(absolutePath + "/" + path);
        if(!file.exists() || !file.isFile())
            throw new IOException("File not found or invalid file: " + file.getAbsolutePath());
        return file;
    }
    
    public final PSObject readPSON(String path) throws IOException, PSONException
    {
        File file = getFile(path);
        return PSON.read(file);
    }
    
    public final PSONUserdataReader readPSON(String path, PSONUserdataReader object) throws IOException, PSONException
    {
        File file = getFile(path);
        return PSON.read(object, file);
    }
    
    
    
    
    /* RESOURCES */
    public static final Resource CONFIG = new Resource("config");
}
