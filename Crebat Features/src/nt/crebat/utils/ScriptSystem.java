/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import nt.ps.PSScript;
import nt.ps.PSState;
import nt.ps.exception.PSException;
import nt.ps.exception.PSRuntimeException;
import nt.ps.lang.PSFunction;
import nt.ps.lang.PSValue;
import nt.ps.lang.PSVarargs;

/**
 *
 * @author mpasc
 */
public final class ScriptSystem
{
    private ScriptSystem() {}
    
    
    public static final void initScripts(String appName)
    {
        PSState state = PSState.createDefaultInstance();
        state.insertDefaultIOUtils();
        state.setNativeValue("PSON", new PSONLib());
        state.setNativeValue("Resource", Resource.PS_RESOURCE);
        state.setNativeValue("register", REGISTER_FUNCTION);
        state.setNativeValue("createGlobalValue", PSFunction.function((arg0, arg1, arg2) -> {
            String name = arg0.toJavaString();
            if(arg2.toJavaBoolean())
            {
                if(!state.hasGlobalValue(name))
                {
                    state.setGlobalValue(name, arg1);
                    return arg1;
                }
                return PSValue.UNDEFINED;
            }
            else
            {
                if(state.hasGlobalValue(name))
                    throw new PSRuntimeException(name + " global value has already exists");
                state.setGlobalValue(name, arg1);
                return arg1;
            }
        }));
        state.setNativeValue("__APP__", PSValue.valueOf(appName));
        
        try
        {
            File initScriptFile = Resource.DATA.getFile("init.pws");
            PSScript script = state.compile(initScriptFile, "ScriptMain");
            script.execute();
        }
        catch(IOException | PSException | PSRuntimeException ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
    
    private static final HashMap<String, PSValue> REGISTERS = new HashMap<>();
    
    public static final PSValue getRegister(String name)
    {
        PSValue value = REGISTERS.get(name);
        if(value == null)
            throw new IllegalArgumentException("Register " + name + " does not exists");
        return value;
    }
    public static final boolean hasRegister(String name) { return REGISTERS.containsKey(name); }
    
    public static final PSVarargs executeRegister(String name) { return getRegister(name).call(); }
    public static final PSVarargs executeRegister(String name, PSValue arg0) { return getRegister(name).call(arg0); }
    public static final PSVarargs executeRegister(String name, PSValue arg0, PSValue arg1) { return getRegister(name).call(arg0, arg1); }
    public static final PSVarargs executeRegister(String name, PSValue arg0, PSValue arg1, PSValue arg2) { return getRegister(name).call(arg0, arg1, arg2); }
    public static final PSVarargs executeRegister(String name, PSValue arg0, PSValue arg1, PSValue arg2, PSValue arg3) { return getRegister(name).call(arg0, arg1, arg2, arg3); }
    public static final PSVarargs executeRegister(String name, PSVarargs args) { return getRegister(name).call(args); }
    public static final PSVarargs executeRegister(String name, PSValue... args) { return getRegister(name).call(PSVarargs.varargsOf(args)); }
    
    private static final PSValue REGISTER_FUNCTION = PSFunction.voidFunction((name, value) -> {
        if(!name.isString())
            throw new PSRuntimeException("Expected a valid string for register name");
        switch(value.getPSType())
        {
            case UNDEFINED:
            case NULL:
                throw new PSRuntimeException("Invalid register value type:" + value);
            default:
                REGISTERS.put(name.toJavaString(), value);
                break;
        }
    });
    
}
