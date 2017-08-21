/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import nt.ps.lang.PSObject;
import nt.ps.lang.PSTuple;
import nt.ps.lang.PSUserdata;
import nt.ps.lang.PSValue;
import nt.ps.lang.PSVarargs;
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
        base = folder == null ? ROOT : new File(ROOT.getAbsolutePath() + "/" + folder);
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
    public static final Resource DATA = new Resource(null);
    public static final Resource CONFIG = new Resource("config");
    public static final Resource SCRIPTS = new Resource("scripts");
    public static final Resource RACES = new Resource("races");
    public static final Resource ATTACKS = new Resource("attacks");
    public static final Resource ITEMS = new Resource("items");
    
    
    
    /* PS Object */
    private static final HashMap<String, PSValue> PS_HASH = new HashMap<String, PSValue>()
    {{
        for(Field field : Resource.class.getDeclaredFields())
        {
            if(!Modifier.isStatic(field.getModifiers()) || field.getType() != Resource.class)
                continue;
            try
            {
                field.setAccessible(true);
                Resource resource = (Resource) field.get(null);
                put(field.getName().toLowerCase(), PSValue.valueOf(resource.absolutePath));
            }
            catch(IllegalAccessException | IllegalArgumentException ex)
            {
                ex.printStackTrace(System.err);
                System.exit(1);
            }
        }
    }};
    
    public static final PSValue PS_RESOURCE = new PSUserdata()
    {
        private final PSTuple names = new PSTuple(PS_HASH.entrySet().stream()
                .map(e -> PSValue.valueOf(e.getKey()))
                .toArray(size -> new PSValue[size]));
        
        @Override public final PSVarargs innerCall(PSValue self) { return names; }
        @Override public final PSVarargs innerCall(PSValue self, PSValue arg0) { return names; }
        @Override public final PSVarargs innerCall(PSValue self, PSValue arg0, PSValue arg1) { return names; }
        @Override public final PSVarargs innerCall(PSValue self, PSValue arg0, PSValue arg1, PSValue arg2) { return names; }
        @Override public final PSVarargs innerCall(PSValue self, PSValue arg0, PSValue arg1, PSValue arg2, PSValue arg3) { return names; }
        @Override public final PSVarargs innerCall(PSValue self, PSVarargs args) { return names; }
        
        @Override
        public final PSValue getProperty(String name)
        {
            PSValue value = PS_HASH.get(name);
            return value == null ? PSValue.UNDEFINED : value;
        }
    };
}
