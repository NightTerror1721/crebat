/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat.utils;

import nt.ps.lang.PSObject;
import nt.ps.lang.PSValue;
import static nt.ps.lang.PSValue.UNDEFINED;
import static nt.ps.lang.PSValue.valueOf;

public abstract class NamedTableObject<NTO extends NamedTableObject> extends TableObject<NTO>
{
    private final String name;
    
    public NamedTableObject(PSObject object)
    {
        super(object);
        this.name = object.getProperty("name").toJavaString();
    }
    
    public final String getName() { return name; }
    
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
            case "name": return valueOf(this.name);
        }
    }
}
