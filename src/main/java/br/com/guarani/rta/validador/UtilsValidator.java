package br.com.guarani.rta.validador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import br.com.guarani.rta.entidade.Campo;
import br.com.guarani.rta.entidade.LinhaErro;
import br.com.guarani.rta.entidade.Registros;
import br.com.guarani.rta.entidade.RelatorioErros;
import br.com.guarani.rta.entidade.TabelasErros;


public class UtilsValidator {
	
	public static int telefone_mask = 0;
	public static int data_mask = 0;
	public static Registros registro;	
	public static List<Registros> registros;
	public UtilsValidator(){
		registro = new Registros();
		registros = new ArrayList<>();
	}
	
	
	public boolean verifyIsNull(Campo campo, String part, String nulo){
		if(nulo.contains("N�o")){
				if(part != null && !part.isEmpty()) {
					return true;
				}else{ 
					registro = new Registros(campo.getNomef(), " Nulo.", "N�o Nulo.", " Campo n�o pode ser nulo.");
				    registros.add(registro);	
				 return false;
				}	
			}
		 else
			return false;
	}
	
	public  boolean verifyIsString(String part){
		if(part.contains("[a-zA-Z]+")) return true;
		else
		return false;	
	}	
	
	
	public static  boolean isReal(String part){
		try {
			Double.parseDouble(part);
			return true;
		} catch (Exception e) {
			return false;
		}
	
	}	
	
	public  boolean verificaTamanho(String basename, Campo campo, Integer tamBd, Integer tamCarga, String part) throws IOException{
		if(part.isEmpty() || part.equals(null) || tamCarga == 0){
			return true;
		}
		if(tamBd >= tamCarga) {
			return true;
		}  
		else
			registro = new Registros(campo.getNomef(), part, " Tam Max:"+tamBd.toString() , " Tamanho do campo incorreto.");
		    registros.add(registro);	
		    return false;
	}
	
	public static  boolean isTelefone(String numeroTelefone, String camponome) {
		if(numeroTelefone.equals(null) || numeroTelefone.isEmpty() || numeroTelefone.length() == 0){
			return true;
		}
        if(numeroTelefone == null || numeroTelefone.matches("(.((10)|([1-9][1-9]).)\\s9?[6-9][0-9]{3}-[0-9]{4})*") ||
                numeroTelefone.matches("(.((10)|([1-9][1-9]).)\\s[2-5][0-9]{3}-[0-9]{4})*"))
        	return true;
        else
        	registro = new Registros(camponome, numeroTelefone, " (XX) XXXX-XXXX / (XX) XXXXX-XXXX", " Formato Telefone inv�lido");
        	registros.add(registro);
        	telefone_mask ++;
        	
        return false;
    }
	
	
	public static  boolean isCep(String cep, String campo){
		if(cep.matches("\\d{5}-\\d{3}")){
			return true;
		}
		else{
			registro = new Registros(campo, cep, " XX.XXX-XXX", " Formato CEP inv�lido");
        	registros.add(registro);
			return false;
		}
	}
	
	
	
	public static  boolean isCnpj(String cnpj, String campo){
		if(cnpj.matches("^([0-9]{2}[.]?[0-9]{3}[.]?[0-9]{3}[/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[.]?[0-9]{3}[.]?[0-9]{3}[-]?[0-9]{2})$")){
		 	return true;
		}
		else{
			registro = new Registros(campo, cnpj, " XX.XXX.XXX/YYYY-ZZ", " Formato CNPJ inv�lido");
        	registros.add(registro);
			return false;
		}
	}
	
	
	public static boolean isDate(String date, String campo){
		if(date.matches("^((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])$")){
			return true;
		}else{
			registro = new Registros(campo, date, " YYYY/MM/DD", " Formato Data inv�lido");
        	registros.add(registro);
        	data_mask ++;
			return false;
		}
	}
	
	
	
	public static boolean isCpf(String cpf, String campo){
		if(cpf.matches("^([0-9]{2}[.]?[0-9]{3}[.]?[0-9]{3}[/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[.]?[0-9]{3}[.]?[0-9]{3}[-]?[0-9]{2})$")){
		 	return true;
		}
		else{
			registro = new Registros(campo, cpf, " XXX.XXX.XXX-XX", " Formato CPF inv�lido");
        	registros.add(registro);
			return false;
		}
	}
	
	@SuppressWarnings("static-access")
	public static boolean isEmbalagem(String embalagem, String campo){
			String regex = "^(([\\w]{1,6})([;])([0-9]{1,3})([;])([0-9]{1,3})([;])([0-9]{1,3})([;])([@])\\s*)+$";
			/*  D,Q,QM,QMI,@
			  	D = Descri��o (m�ximo 6 caracteres);
				Q = Quantidade;
				QM = Quantidade m�ltipla;
				QMI = Quantidade m�nima;
				@ = Separador de embalagem
			 */    
			if(embalagem.matches(regex)){
				return true; 
			} 
				registro = new Registros(campo, embalagem, " D,Q,QM,QMI,@", "Dados embalagem incorretos");
	        	registros.add(registro);
				return false;
	} 
	
	public static boolean isFrete(String frete, String campo){
		List<String> dados = new ArrayList<>();
		dados.add("C");
		dados.add("F");
		dados.add("S");
		if(dados.contains(frete)){
			return true;
		}
		else
			registro = new Registros(campo, frete, "C - F - S", "Dados inv�lidos");
	    	registros.add(registro);
			return false;		
	}
	
	public static boolean SN(String sn, String campo){
		if(campo.equals("S") || campo.equals("N"))
		return true;
		else 
			registro = new Registros(campo, sn, " S ou N, somente", "Dados inv�lidos");
	    	registros.add(registro);
			return false;
	}
	
	
	private static boolean isEstadoSeparadoPorVirgula(String uf, String campo) {
		String regex = "^((([a-zA-Z]){2})([;]){0,1})*$";
		if (uf.matches(regex)) {
			return true;
		}else{
			registro = new Registros(campo, uf,  " UF separados por v�rgula", "Dados inv�lidos");
	    	registros.add(registro);
	    	return false;
		}
	}
	

	private static boolean isTipoPessoa(String tipo, String campo) {
		if(tipo.equals("R") || tipo.equals("C") || tipo.equals("P") || tipo.equals("F") || tipo.equals("D") || tipo.equals("I")){
			return true;
		}else
			registro = new Registros(campo, tipo,  " R - C - P - F - D - I", "Dados inv�lidos");
    		registros.add(registro);
			return false;
	}
	
	
	private static boolean isTrataLimitCred(String dados, String campo) {
		String regex = "^([0-8])$";
		if(dados.matches(regex)){
			return true;
		}else{
			registro = new Registros(campo, dados,  " 0, 1, 2, 3, 4, 5, 6, 7 ou 8", "Dados inv�lidos");
			registros.add(registro);
			return false;
		}
	}
	
	private static boolean isTipoComissao(String dados, String campo){
		String regex = "^([NSM])$";
		if(dados.matches(regex)){
			return true;
		}else{
			registro = new Registros(campo, dados,  " N- (Comiss�o Flex�vel), S - (Verba), M - (Margem C.)", "Dados inv�lidos");
			registros.add(registro);
			return false;
		}
	}
	
	private static boolean isPoliticaPrecos(String dados, String campo){
		String regex = "^([012])$";
		if(dados.matches(regex)){
			return true;
		}else{
			registro = new Registros(campo, dados,  " 0 - Libera, 1 - Trava, 2 - Ignora ", "Dados inv�lidos");
			registros.add(registro);
			return false;
		}
	}
	
	private static boolean isPraziMinimoEnt(String dados, String campo){
		if(dados.equals("999") || dados.equals("0")) return true;
		else
			registro = new Registros(campo, dados,  " 999 - Ilimitado, 0 - Desebilitado ", "Dados inv�lidos");
			registros.add(registro);
			return false;
	}


	public static void validaAtributos(Campo campo, String part){
		int atributo;
		try {
			if(campo.getAtributos() == null){
			}else{
				atributo =	campo.getAtributos().getId();		
				if(atributo == 1){
					UtilsValidator.isTelefone(part, campo.getNomef());
				}
				if(atributo == 2){
					UtilsValidator.isCnpj(part, campo.getNomef());
				}
				if(atributo == 3){
					UtilsValidator.isCpf(part, campo.getNomef());
				}
				if(atributo == 4){
					UtilsValidator.isDate(part, campo.getNomef());
				}
				if(atributo == 5){ 
					UtilsValidator.isCep(part, campo.getNomef());
				}
				if(atributo == 6){
					UtilsValidator.isEmbalagem(part, campo.getNomef());
					
				}if(atributo == 7){
					UtilsValidator.isFrete(part, campo.getNomef());
					
				}if(atributo == 8){
					UtilsValidator.SN(part, campo.getNomef());
					
				}if(atributo == 9){
					UtilsValidator.isEstadoSeparadoPorVirgula(part, campo.getNomef());
					
				}if(atributo == 10){
					UtilsValidator.isTipoPessoa(part, campo.getNomef());
				
				}if(atributo == 11){
					UtilsValidator.isTrataLimitCred(part, campo.getNomef());
					
				}if(atributo == 12){
					UtilsValidator.isTipoComissao(part, campo.getNomef());
					
				}if(atributo == 13){
					UtilsValidator.isPoliticaPrecos(part, campo.getNomef());
					
				}if(atributo == 14){
					UtilsValidator.isPraziMinimoEnt(part, campo.getNomef());
					
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	





	public static List<String> checaCaractere(String part, String caractere, Integer tamMax) {

		String novalinha = "";
		
	    novalinha = novalinha + part;
	    
	    List<String> campos = new ArrayList<String>();
	    
	    Character lendo;   
	    int j = 0, i = 0;
	    int tam_string = novalinha.length();
	    int start = 0;

	    while(j < tam_string){
	    	lendo = novalinha.charAt(j);
	    	i++;
		        if (caractere.equalsIgnoreCase(lendo.toString())) {
		            campos.add(novalinha.substring(start, j));
		            start = j+1;
		            i = 0;
		        }
	        j++;
	    }
	   
	   return campos;
	}


	public Registros getRegistro() {
		return registro;
	}


	public void setRegistro(Registros registro) {
		this.registro = registro;
	}


	public List<Registros> getRegistros() {
		return registros;
	}


	public void setRegistros(List<Registros> registros) {
		this.registros = registros;
	}
	
	public int getTelefone_mask() {
		return telefone_mask;
	}

}
