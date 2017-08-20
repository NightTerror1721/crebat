/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat.utils;

import java.io.File;
import java.io.IOException;
import nt.ps.exception.PSRuntimeException;
import nt.ps.lang.PSFunction;
import nt.ps.lang.PSUserdata;
import nt.ps.lang.PSValue;
import nt.ps.pson.PSON;
import nt.ps.pson.PSONException;

/**
 *
 * @author mpasc
 */
public final class PSONLib extends PSUserdata
{
    @Override
    public final PSValue getProperty(String name)
    {
        switch(name)
        {
            default: return UNDEFINED;
            case "read": return READ;
            case "write": return WRITE;
        }
    }
    
    private static final PSValue
            READ = PSFunction.function((file) -> {
                try
                {
                    return PSON.read(new File(file.toJavaString()));
                }
                catch(IOException | PSONException ex) { throw new PSRuntimeException(ex); }
            }),
            WRITE = PSFunction.voidFunction((object, file, wrapped) -> {
                if(!object.isObject())
                    throw new PSRuntimeException("Required a valid object to transform in PSON");
                try
                {
                    PSON.write(object.toPSObject(), wrapped.toJavaBoolean(), new File(file.toJavaString()));
                }
                catch(IOException ex) { throw new PSRuntimeException(ex); }
            });
}
