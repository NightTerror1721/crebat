/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat.utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import nt.ps.lang.PSObject;
import nt.ps.lang.PSUserdata;
import nt.ps.lang.PSValue;
import nt.ps.pson.PSONException;

public abstract class TableObject<TO extends TableObject> extends PSUserdata implements Comparable<TO>
{
    final int id;
    
    public TableObject(PSObject object)
    {
        this.id = object.getProperty("id").toJavaInt();
    }
    
    public final int getId() { return id; }
    
    protected abstract PSValue innerGetProperty(String name);
    
    @Override
    public PSValue getProperty(String name)
    {
        switch(name)
        {
            default: {
                PSValue value = innerGetProperty(name);
                return value == null ? UNDEFINED : value;
            }
            case "id": return valueOf(id);
        }
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public final boolean equals(Object obj)
    {
        return this == obj || (obj != null && getClass() == obj.getClass() &&
                this.id == ((TableObject) obj).id);
    }
    
    @Override
    public final int compareTo(TO o)
    {
        return id > o.id ? 1 : id < o.id ? -1 : 0;
    }
    
    
    private static final Class<?>[] CNS_TYPES = { PSObject.class };
    protected static final <TO extends TableObject> Map<Integer, TO> importTable(String tableName, Class<TO> itemsClass)
    {
        try
        {
            Constructor<TO> cns = itemsClass.getDeclaredConstructor(CNS_TYPES);
            cns.setAccessible(true);
            
            HashMap<Integer, TO> map = new HashMap<>();
            Resource.CONFIG.readPSON(tableName + ".pson", (name, value) -> {
                if(!name.equals("table") || !value.isArray())
                    return;
                for(PSValue arrayValue : value.toJavaList())
                {
                    if(!arrayValue.isObject())
                        continue;
                    try
                    {
                        TO to = cns.newInstance(arrayValue.toPSObject());
                        if(map.containsKey(to.id))
                            System.err.println("table key " + to.id + " has already exists in table " + tableName);
                        map.put(to.id, to);
                    }
                    catch(IllegalAccessException | IllegalArgumentException |
                            InstantiationException | InvocationTargetException ex)
                    {
                        ex.printStackTrace(System.err);
                    }
                }
            });
            return map;
        }
        catch(IOException | NoSuchMethodException |
                SecurityException | PSONException ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
            return null;
        }
    }
}
