package br.com.guarani.rta.rest.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.sun.jersey.server.wadl.WadlApplicationContext;

import br.com.guarani.rta.dao.campo.CampoDAO;
import br.com.guarani.rta.entidade.CabecalhoErros;
import br.com.guarani.rta.entidade.Campo;
import br.com.guarani.rta.entidade.Projeto;
import br.com.guarani.rta.entidade.Projetos;
import br.com.guarani.rta.entidade.RelatorioErros;
import br.com.guarani.rta.entidade.TabelaErro;
import br.com.guarani.rta.entidade.TabelasErros;
import br.com.guarani.rta.validador.TesteFile;
import br.com.guarani.rta.validador.UtilsValidator;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


//@Scope(value=WebApplicationContext.SCOPE_REQUEST)
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component 
@Path("/relatorios")
public class RelatorioValidadorResource {
	 	  
			private String folderUser = null;
		 
			@Autowired
			private TesteFile relatorios;
			 
			@GET
			@Consumes("text/html")
			@Produces(MediaType.APPLICATION_JSON)
			public String listErros(@Context ServletContext ctx) throws JsonGenerationException, JsonMappingException, IOException{
			ObjectMapper mapper = new ObjectMapper();
		    File folder = new File(ctx.getRealPath("/arquivos/" + folderUser));
		    CabecalhoErros cabecalhoErros = new CabecalhoErros();
			List<TabelaErro> tabErros = new ArrayList<TabelaErro>();
			cabecalhoErros = relatorios.listaErros(folder);
			folderUser = null;
			return mapper.writeValueAsString(cabecalhoErros);
			} 
		   
			@ResponseStatus(value=HttpStatus.OK)
			@POST
			@Produces(MediaType.MULTIPART_FORM_DATA)
			@Consumes(MediaType.MULTIPART_FORM_DATA)
		    @Path("/upload")
		    public void listValid(@Context HttpServletRequest req, @Context ServletContext ct) throws IOException, FileUploadException {

				/*HttpSession session= req.getSession(true);
		    	Object foo = session.getAttribute("foo");
		    	if (foo!=null) {
		    		System.out.println("XXXXXXXXXXX: "+ foo.toString());
		    	} else {
		    		foo = "bar";
		    		session.setAttribute("XXXXXXXXXXX: "+"foo", "bar");
		    	}*/
				
				 
				
				if(folderUser == null){		
					folderUser = RandomStringUtils.randomAlphabetic(10);			
				    File folder = new File(ct.getRealPath("/arquivos/" + folderUser));
				    folder.mkdir();
				}
				
				ServletFileUpload fileUpload = new ServletFileUpload();
				FileItemIterator iterator = fileUpload.getItemIterator(req);
				while (iterator.hasNext()) {	
	                   FileItemStream item = iterator.next();
	                   File file = streamToFile(req, item, "arquivos/" + folderUser);   				   
	                   			if(isZip(file.getName())){
			           				String realPath  = req.getSession().getServletContext().getRealPath("/arquivos");
			                	    unZip(file.getAbsolutePath(), realPath +"/"+folderUser);
								}
	       		 	    }
				} 
			
			
			
						
				private boolean isZip(String name) {
					String extension = name.substring(name.lastIndexOf(".") + 1, name.length());
						if(extension.equals("zip")){
							return true;
						}
					return false;
				}
						
		
				private void unZip(String zip, String folderStract){
			         try {
						ZipFile zipFile = new ZipFile(zip);
						zipFile.extractAll(folderStract);
						zipFile.getFile().delete();
					} catch (ZipException e) {
						e.printStackTrace();
					}
				 }
				
		 					 
				private File streamToFile (HttpServletRequest req, FileItemStream item, String baseFolder) throws IOException {            
					String realPath  = req.getSession().getServletContext().getRealPath("/" + baseFolder);
				    File file = new File(realPath + "/" + item.getName());
				    InputStream inputStream = item.openStream();		
					    try (FileOutputStream out = new FileOutputStream(file)) {
					        IOUtils.copy(inputStream, out);
					        inputStream.close();
					    }
				    return file;            
				}
				
				
				private boolean isAuthenticated(){
					
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					Object principal = authentication.getPrincipal();
					
					if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
						return true;
					}
					return false;
				}

}
