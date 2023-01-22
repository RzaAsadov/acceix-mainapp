/*
 * The MIT License
 *
 * Copyright 2022 Rza Asadov (rza at asadov dot me).
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



 
import org.acceix.frontend.crud.loaders.CustomAssetsLoader;
import org.acceix.frontend.crud.loaders.CustomViewsLoader;
import org.acceix.frontend.crud.loaders.DbStoredLoader;
import org.acceix.frontend.crud.loaders.ObjectLoader;
import org.acceix.frontend.crud.loaders.ScriptLoader;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.acceix.frontend.builtin.dbstored.BuildInDbStoredLoader;
import org.acceix.frontend.builtin.functions.BuiltInFunctionsLoader;
import org.acceix.frontend.builtin.objects.BuiltInObjectsLoader;
import org.acceix.frontend.core.accounts.Accounts;
import org.acceix.frontend.core.NetondoMaintanance;
import org.acceix.frontend.core.accounts.Setup;
import org.acceix.frontend.crud.CoreModule;
import org.acceix.frontend.crud.ViewModule;
import org.acceix.frontend.crud.loaders.AdminAssetsLoader;
import org.acceix.frontend.crud.loaders.AdminViewsLoader;
import org.acceix.frontend.crud.loaders.DbEventsLoader;
import org.acceix.frontend.crud.loaders.DbTablesLoader;
import org.acceix.frontend.crud.loaders.DbTriggersLoader;
import org.acceix.frontend.crud.loaders.DbViewsLoader;
import org.acceix.frontend.crud.loaders.FunctionLoader;
import org.acceix.frontend.files.UploadManager;
import org.acceix.frontend.location.LocationManager;
import org.acceix.ide.modules.adminassets.NCodeAdminAssets;
import org.acceix.ide.modules.dbstored.NCodeDbStored;
import org.acceix.ide.modules.apifunctions.NCodeFunctions;
import org.acceix.ide.modules.objects.NCodeObjects;
import org.acceix.ide.modules.scripts.NCodeScripts;
import org.acceix.ide.modules.adminviews.NCodeAdminViews;
import org.acceix.ide.modules.customviews.NCodeCustomViews;
import org.acceix.ide.modules.customassets.NCodeCustomAssets;
import org.acceix.ide.modules.dbevents.NCodeDbEvents;
import org.acceix.ide.modules.tables.NCodeDbTables;
import org.acceix.ide.modules.dbtriggers.NCodeDbTriggers;
import org.acceix.ide.modules.dbviews.NCodeDbViews;
import org.acceix.ndatabaseclient.mysql.DbMetaData;

/**
 *
 * @author zrid
 */
public class MainApp {

    private static String JRE_PATH = "";
    private static String INSTALL_PATH = "";
    private static String APP_NAME = "";
    private static String CONFIG_FILE_PATH = "";
    private static String APP_PATH = "";
    private static String LOGDIR_PATH = "";
    
    
    private final static int APP_CREATE_MODE = 1;
    private final static int APP_START_MODE = 2;
    private final static int START_ALL_APPS = 3;
    
    
    private static int  currentStartMode = 0;

    private static final Map<String, Object> ENVS = new HashMap<>();

    public static int getCurrentStartMode() {
        return currentStartMode;
    }

    public static void setCurrentStartMode(int currentStartMode) {
        MainApp.currentStartMode = currentStartMode;
    }
        

    public static String getJRE_PATH() {
        return JRE_PATH;
    }

    public static String getINSTALL_PATH() {
        return MainApp.INSTALL_PATH;
    }

    public static void setINSTALL_PATH(String INSTALL_PATH) {
        MainApp.INSTALL_PATH = INSTALL_PATH;
    }

    public static String getLOGDIR_PATH() {
        return LOGDIR_PATH;
    }

    public static Map<String, Object> getENVS() {
        return ENVS;
    }

    public static void setAPP_PATH(String APP_PATH) {
        MainApp.APP_PATH = APP_PATH;
    }

    public static String getAPP_PATH() {
        return APP_PATH;
    }

    public static String getAPP_NAME() {
        return APP_NAME;
    }

    public static void setAPP_NAME(String APP_NAME) {
        MainApp.APP_NAME = APP_NAME;
    }
    
    

    public static void setCONFIG_FILE_PATH(String CONFIG_FILE_PATH) {
        MainApp.CONFIG_FILE_PATH = CONFIG_FILE_PATH;
    }

    public static String getCONFIG_FILE_PATH() {
        return CONFIG_FILE_PATH;
    }
    
    
    
    

    public static void main(String[] args) throws FileNotFoundException {
        
        
        if (System.getProperty("user.name").equals("root")) {
            System.err.println("Running under root prohibited ! Please use another user !");
            System.exit(0);
        }        

        try {
            
            LinkedHashMap arguments = ArgumentParser.parseArguments(args);
            setCONFIG_FILE_PATH((String)arguments.get("CONFIG_FILE_PATH"));
            setAPP_NAME((String)arguments.get("APP_NAME"));            
            
        } catch (ParseException | org.apache.commons.cli.ParseException  ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        

            
            setPaths();
            
            
            ////////////////////////////////////////////////////////////////////////
            ////////////////// APP CREATE MODE /////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////
            if (getAPP_NAME() != null && getCONFIG_FILE_PATH() == null) {
                
                    setCurrentStartMode(MainApp.APP_CREATE_MODE);

                    ENVS.put("isAppCreateMode", true); 

                    ConfigSetup.createAppDirectories(getINSTALL_PATH(),getAPP_NAME());

                    ConfigSetup.createConfig(getCONFIG_FILE_PATH(), getINSTALL_PATH(), getENVS());
                    
                    
            ////////////////////////////////////////////////////////////////////////
            ////////////////// APP START MODE /////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////                
            } else if (getAPP_NAME() == null && getCONFIG_FILE_PATH() != null) {
                
                    setCurrentStartMode(MainApp.APP_START_MODE);

                    ENVS.put("isAppCreateMode", false); 



                    if (!ConfigSetup.readConfig(getCONFIG_FILE_PATH(),getENVS()) ) {
                        System.out.println("Config file '" + getCONFIG_FILE_PATH() + "' unreadable !");
                        System.exit(0); // Exit if troubles with reading  config file;
                    }                

                    loadAppAssets();
                
            ////////////////////////////////////////////////////////////////////////
            ////////////////// START ALL APPS MODE /////////////////////////////////
            ////////////////////////////////////////////////////////////////////////                
            } else if (getAPP_NAME() == null && getCONFIG_FILE_PATH() == null) {
                
                    setCurrentStartMode(MainApp.START_ALL_APPS);

                    ENVS.put("isAppCreateMode", false); 
                
            ////////////////////////////////////////////////////////////////////////
            //////////////// Exit because wrong arguments //////////////////////////
            ////////////////////////////////////////////////////////////////////////                
            } else if (getAPP_NAME() != null && getCONFIG_FILE_PATH() != null) {
                System.out.println("Create mode and start mode can not be used in same time !");
                System.exit(0);
            }
            
           
            

        
        if ((ENVS.get("database_ssh_user") != null) 
                && (ENVS.get("database_ssh_host") != null)) {
              connecToVpn();
        }


        
        // Static data set
        DbMetaData.setColumnTypes();
        DbMetaData.setColumnLength();

        
        // Start web server
        WebService mainWebService = new WebService(ENVS);
        
        
        mainWebService.addNetondoModule(new Setup());
        mainWebService.addNetondoModule(new Accounts());

        mainWebService.addNetondoModule(new NetondoMaintanance());
        
        mainWebService.addNetondoModule(new NCodeObjects());
        mainWebService.addNetondoModule(new NCodeFunctions());
        
        mainWebService.addNetondoModule(new NCodeDbStored());
        mainWebService.addNetondoModule(new NCodeDbTriggers());
        mainWebService.addNetondoModule(new NCodeDbViews());
        mainWebService.addNetondoModule(new NCodeDbEvents());
        mainWebService.addNetondoModule(new NCodeDbTables());
        
        mainWebService.addNetondoModule(new NCodeScripts());
        
        mainWebService.addNetondoModule(new NCodeAdminViews());
        mainWebService.addNetondoModule(new NCodeCustomViews());
        
        mainWebService.addNetondoModule(new NCodeCustomAssets()); 
        mainWebService.addNetondoModule(new NCodeAdminAssets()); 
        
        mainWebService.addNetondoModule(new UploadManager());
        mainWebService.addNetondoModule(new LocationManager());

       
        mainWebService.addNetondoModule(new CoreModule());
       
        mainWebService.addNetondoModule(new ViewModule());         
      

        try {
            mainWebService.run();
            System.out.println("AcceIX Platform started.");
        } catch (Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        



    }

    
    private static void setPaths() {

        // Get install path from executed jar
        String jarPath = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            jarPath = URLDecoder.decode(jarPath, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        jarPath = new File(jarPath).getPath();

        if (INSTALL_PATH.equals("")) {

            INSTALL_PATH = Paths.get(jarPath).toFile().getParent();

            if (INSTALL_PATH.endsWith("build")) {
                INSTALL_PATH = INSTALL_PATH.substring(0, INSTALL_PATH.length() - 6);
            } else if (INSTALL_PATH.endsWith("dist")) {
                INSTALL_PATH = INSTALL_PATH.substring(0, INSTALL_PATH.length() - 5);
            }

        }

        // Set server log path
        LOGDIR_PATH = INSTALL_PATH + File.separator + "logs";

        if (!new File(getLOGDIR_PATH()).exists()) {
            new File(getLOGDIR_PATH()).mkdir();
        }

        // Set JRE Path
        JRE_PATH = INSTALL_PATH + File.separator + "jre";

    }
    

    
    private static void connecToVpn() {
        
                System.out.print("Trying to establish ssh tunnel to  database....");

                SshTunnelClient sshTunnelClient = new SshTunnelClient();

                sshTunnelClient.setLocalhost("localhost");
                sshTunnelClient.setLocalport((String) ENVS.get("database_port"));
                sshTunnelClient.setUser((String) ENVS.get("database_ssh_user"));
                sshTunnelClient.setRemotehost((String) ENVS.get("database_ssh_host"));
                sshTunnelClient.setRemoteport("3306");
                //sshTunnelClient.setSshKeyFilepath((String)ENVS.get("database_ssh_keyfilepath"));

                sshTunnelClient.start();

                while (!sshTunnelClient.isConnectedToSSH()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                System.out.println("......DONE");        
    }
    
    

    

    
    private static void loadAppAssets() {
        
                FunctionLoader.setGlobalEnvs(getENVS());
                ObjectLoader.setGlobalEnvs(getENVS());
                DbStoredLoader.setGlobalEnvs(getENVS());
                DbTriggersLoader.setGlobalEnvs(getENVS());
                DbViewsLoader.setGlobalEnvs(getENVS());
                DbEventsLoader.setGlobalEnvs(getENVS());

                new BuiltInObjectsLoader().loadObjects(); // Load builtin objects
                new BuiltInFunctionsLoader().loadFunctions(); // Load builtin functions
                new BuildInDbStoredLoader().loadDbStored(); // Load builtin db stored            

                new FunctionLoader().loadAll(getENVS().get("functions_path").toString());


                new ObjectLoader().loadAll(getENVS().get("objects_path").toString());

                new DbStoredLoader().loadAll(getENVS().get("dbstored_path").toString());
                
                new DbTriggersLoader().loadAll(getENVS().get("dbtriggers_path").toString());
                
                new DbViewsLoader().loadAll(getENVS().get("dbviews_path").toString());
                
                new DbEventsLoader().loadAll(getENVS().get("dbevents_path").toString());
                
                new DbTablesLoader().loadAll(getENVS().get("dbtables_path").toString()); 
                
                new ScriptLoader().loadAll(getENVS().get("scripts_path").toString());
                
                new CustomViewsLoader().loadAll(getENVS().get("custom_views_path").toString());
                
                new CustomAssetsLoader().loadAll(getENVS().get("custom_web_assets_path").toString()); 
                
                new AdminViewsLoader().loadAll(getENVS().get("admin_views_path").toString());
            
                new AdminAssetsLoader().loadAll(getENVS().get("admin_web_assets_path").toString());                
                
    }
    
  
} // END OF CLASS