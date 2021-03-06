package br.com.guarani.rta.test;

import java.util.ArrayList;
import java.util.List;
 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.com.guarani.rta.dao.projeto.ProjetoDAO;
import br.com.guarani.rta.dao.projeto.ProjetoDAOimpl;
import br.com.guarani.rta.entidade.Projeto;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context.xml" })
public class ProjetoTeste {
	
	@Autowired
	public ProjetoDAOimpl dao;
	 
	@Test 
	public void projetoLista(){
		List<Projeto> list = dao.projetosUsuario("00000000000");
		System.out.println(list);
	}
}
