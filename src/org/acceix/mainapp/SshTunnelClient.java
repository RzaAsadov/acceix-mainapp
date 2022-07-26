/*
 * The MIT License
 *
 * Copyright 2022 Rza Asadov (rza dot asadov at gmail dot com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.acceix.mainapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;





/**
 *
 * @author zrid
 */
public class SshTunnelClient extends Thread {

   
    private String user,remotehost,localhost,remoteport,localport,sshKeyFilepath;
    
    private Process sshProc;
    
    private static boolean sshConnected=false;

    public void setLocalhost(String localhost) {
        this.localhost = localhost;
    }

    public void setLocalport(String localport) {
        this.localport = localport;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setRemotehost(String remotehost) {
        this.remotehost = remotehost;
    }

    public void setRemoteport(String remoteport) {
        this.remoteport = remoteport;
    }

    public void setSshKeyFilepath(String sshKeyFilepath) {
        this.sshKeyFilepath = sshKeyFilepath;
    }
    
    
    @Override
    public void run () {
        
        String sshCmd = "ssh -v -N -L " + localport + ":" + localhost + ":" + remoteport + " "  + user + "@" + remotehost;
        
        //System.out.println("Running cmd:" + sshCmd);
        
        
        
        ProcessBuilder processBuilder = new ProcessBuilder("bash","-c",sshCmd);
        
        try {
            
            new ProcessBuilder("bash","-c","pkill -f \"" + sshCmd + "\"").start();
            
            sshProc = processBuilder.start();
            
            
            
            new BufferedReader(new InputStreamReader(sshProc.getErrorStream())).lines()
            .forEach((line) -> {
                //System.out.println(line);
                if (line.contains("Local forwarding listening on")) {
                    sshConnected=true;
                    //System.out.println("YESYESYEDS");
                }
            });
            
        } catch (IOException ex) {
            Logger.getLogger(SshTunnelClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
 
  

    public boolean isConnectedToSSH() {
       
            return sshConnected;
    
    }   

 
}


