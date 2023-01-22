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


import org.acceix.frontend.helpers.ModuleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.acceix.frontend.core.Engine;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;


/**
 *
 * @author Rza Asadov <rza at asadov dot me>
 */
public class WebService {
    
    
        private static final int MAX_WEBTHREADS = 2000;    
    

        private final Map<String,Object> envs;
        
        private  final List<ModuleHelper> modules = new ArrayList<>();

        public WebService(Map<String, Object> envs) {
            this.envs = envs;
        }
        
        public void addNetondoModule (ModuleHelper module) {
                module.setGobalEnv(envs);
                module.construct();            
                modules.add(module);
        }
    
        public void run() throws Exception {
                Engine.setEnvs(envs);
                Engine.setModules(modules);
                startServer();
        }
        
        
        public void startServer() throws Exception {
            


            QueuedThreadPool queuedthreadPool = new QueuedThreadPool();
            queuedthreadPool.setMaxThreads(MAX_WEBTHREADS);            

            Server server = new Server();
            

    
                    
            /*ServletContextHandler contextFiles = new ServletContextHandler(ServletContextHandler.SESSIONS);
                ResourceHandler resource_handler = new ResourceHandler();
                resource_handler.setDirectoriesListed(true);
                resource_handler.setWelcomeFiles(new String[]{ envs.get("index_file").toString() });
                resource_handler.setResourceBase(envs.get("admin_web_assets_path").toString());
                //resource_handler.setResourceBase(EmptyClass.class.getClassLoader().getResource("frontend/web/assets").toExternalForm());
            contextFiles.setContextPath(envs.get("context_path").toString());
            contextFiles.setHandler(resource_handler);
            
            ServletContextHandler contextFilesCustom = new ServletContextHandler(ServletContextHandler.SESSIONS);
                ResourceHandler resource_handlerCustom = new ResourceHandler();
                resource_handlerCustom.setDirectoriesListed(true);
                resource_handlerCustom.setWelcomeFiles(new String[]{ envs.get("index_file").toString() });
                resource_handlerCustom.setResourceBase(envs.get("custom_web_assets_path").toString());


                //resource_handler.setResourceBase(EmptyClass.class.getClassLoader().getResource("frontend/web/assets").toExternalForm());
            contextFilesCustom.setContextPath("/custom");
            contextFilesCustom.setHandler(resource_handlerCustom);
            
          
           
*/
                Engine.envs=envs;
                Engine.modules=modules;
                
                ServletHolder engineHolder = new ServletHolder(Engine.class);
            
                long maxFileSize = Long.parseLong((String)envs.get("max_upload_size"));
                long maxRequestSize = Long.parseLong((String)envs.get("max_form_content_size"));
                int fileSizeThreshold = Integer.parseInt((String)envs.get("file_size_threshold"));
                


            //engineHolder.getRegistration().setMultipartConfig(new MultipartConfigElement((String)envs.get("upload_tmp"), maxFileSize, maxRequestSize, fileSizeThreshold));            

            ////////////////////////////// PLATFORM ////////////////
            ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
                holderHome.setInitParameter("resourceBase",envs.get("admin_web_assets_path").toString());
                holderHome.setInitParameter("dirAllowed","true");
                holderHome.setInitParameter("pathInfoOnly","true");
          
            
            ServletContextHandler contextServlet = new ServletContextHandler(ServletContextHandler.SESSIONS);
                contextServlet.setMaxFormContentSize(Integer.parseInt(envs.get("max_form_content_size").toString()));
                contextServlet.setContextPath(envs.get("context_path").toString());
                contextServlet.setWelcomeFiles(new String[]{ envs.get("index_file").toString() });            
                contextServlet.addServlet(engineHolder,"/engine"); 
                //contextServlet.addServlet(className, pathSpec)
                contextServlet.addServlet(holderHome,"/*");
                
            //////////////////////////////// CUSTOM /////////////////
            ServletHolder holderCustom = new ServletHolder("static-home", DefaultServlet.class);
                holderCustom.setInitParameter("resourceBase",envs.get("custom_web_assets_path").toString());
                holderCustom.setInitParameter("dirAllowed","true");
                holderCustom.setInitParameter("pathInfoOnly","true");
                
                
            ServletContextHandler contextServletCustom = new ServletContextHandler(ServletContextHandler.SESSIONS);
                contextServletCustom.setMaxFormContentSize(Integer.parseInt(envs.get("max_form_content_size").toString()));
                contextServletCustom.setContextPath("/custom");
                contextServletCustom.setWelcomeFiles(new String[]{ envs.get("index_file").toString() });            
                contextServletCustom.addServlet(holderCustom,"/*");
                
                
                RewriteHandler rewriteHandler = new RewriteHandler();
                rewriteHandler.setRewriteRequestURI(true);
                rewriteHandler.setRewritePathInfo(false);

                rewriteHandler.setOriginalPathAttribute("requestedPath");


                /*RewriteRegexRule rule1 = new RewriteRegexRule();
                rule1.setRegex("^/custom/.*");
                rule1.setReplacement("/custom/index.html");

                rewriteHandler.addRule(rule1);        

                rewriteHandler.setHandler(contextServletCustom); */

                //ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
                //errorHandler.addErrorPage(404, "/index.html");
                //contextServletCustom.setErrorHandler(errorHandler);
            
            FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
            filterHolder.setInitParameter("allowedOrigins", "*");
            filterHolder.setInitParameter("allowedMethods", "GET, POST");

            
            contextServlet.addFilter(filterHolder, "/*", null);

            
  //          ServletContextHandler contextDefault = new ServletContextHandler(ServletContextHandler.SESSIONS);
  //          contextDefault.setContextPath("/*"); 
  



            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[] { /*contextDefault,contextFiles,*/contextServletCustom,contextServlet});


            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());


                    
            if (envs.get("http_port") != null) {

                    ServerConnector connector = new ServerConnector(server);
                    connector.setPort(Integer.parseInt(envs.get("http_port").toString()));                 
                    server.addConnector(connector);
                    System.out.println("Added web server http port:" + envs.get("http_port"));
            } 
            
            if (envs.get("https_port") != null) {

                    SslContextFactory sslContextFactory = new SslContextFactory();
                    sslContextFactory.setKeyStorePath(envs.get("ssl_keystore_path").toString()); 

                    sslContextFactory.setKeyStorePassword(envs.get("ssl_keystore_password").toString());
                    sslContextFactory.setKeyManagerPassword(envs.get("ssl_keymanager_password").toString());

                    ServerConnector connector = new ServerConnector(server,new SslConnectionFactory(sslContextFactory, "http/1.1"),new HttpConnectionFactory(https));
                    connector.setPort(Integer.parseInt(envs.get("https_port").toString()));                 
                    server.addConnector(connector);
                    System.out.println("Added web server https port:" + envs.get("https_port"));
                    System.out.println("SSL keystore at: " + envs.get("ssl_keystore_path"));
                    
            }
            
            if (server.getConnectors().length > 0) {
                    server.setHandler(contexts);        
                    server.start();
            }

    }
        
    
}
