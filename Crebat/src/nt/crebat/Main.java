/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat;

import java.io.IOException;
import nt.crebat.utils.ScriptSystem;
import nt.ps.compiler.exception.CompilerError;

/**
 *
 * @author Asus
 */
public final class Main
{
    public static void main(String[] args) throws IOException
    {
        CompilerError.setDebugMode(true);
        ScriptSystem.initScripts("BPS");
    }
}
