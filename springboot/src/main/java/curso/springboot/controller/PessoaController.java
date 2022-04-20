package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;

	// URL inicial  (/) 
	// contoler do inicio
	@RequestMapping(method = RequestMethod.GET, value = "**/cadastropessoa")
	public ModelAndView inicio() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		//Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//lista todas as pessoas na view
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("profissoes", profissaoRepository.findAll()); //passando os atributos de profisoes para a tela
		return modelAndView;
	}
	
	
	//FAZENDO TODA A PAGINAÇAO DA TELA 
	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoaPorPaginacao(@PageableDefault(size = 5)Pageable pageable,
			ModelAndView model, @RequestParam("nomepesquisa") String nomepesquisa) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);//carregando do banco 

		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoaobj", new Pessoa());
		model.addObject("nomepesquisa", nomepesquisa);
		model.setViewName("cadastro/cadastropessoa");
		
		return model;
	}
	

	
	
	// metodo que esta salvando e listando ao mesmo tempo
	// os ** ingnora tudo que vinher antes da url
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa", consumes = {"multipart/form-data"})
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file) throws IOException {

		//System.out.println(file.getContentType());
		//System.out.println(file.getOriginalFilename());
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if (bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")))); //consulta por paginaçao
			modelAndView.addObject("pessoaobj", pessoa);

			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // vem das anotação @notEmpty e outras

			} 

			modelAndView.addObject("msg", msg);
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			return modelAndView;
		}
		
		if(file.getSize()> 0) {//cadastrando um curriculo
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		}else {
			if(pessoa.getId() !=null && pessoa.getId() > 0) {// editando pessoa com todos os dados
				
				Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();//carrega do banco a pessoa 
				
				pessoa.setCurriculo(pessoaTemp.getCurriculo());
				pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
			}
		}
		
		pessoaRepository.save(pessoa); //salvandono banco
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));//consulta por paginaçao
		andView.addObject("pessoaobj", new Pessoa()); //passando um objeto vazio
		
		return andView;
	}

	
	
	
	// metodo para listar pessoas
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoa")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));//consulta por paginaçao
		andView.addObject("pessoaobj", new Pessoa());
		
		return andView;
	}
	
	

	// metodo para editar
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa){
		
				Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);//carregando do banco
				
				ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
				modelAndView.addObject("pessoaobj", pessoa.get());
				modelAndView.addObject("profissoes", profissaoRepository.findAll());
				return modelAndView;		
	}
	
	// metodo para excluir 
		@GetMapping("/removerpessoa/{idpessoa}")
		public ModelAndView exluir(@PathVariable("idpessoa") Long idpessoa){
								
			pessoaRepository.deleteById(idpessoa); //deletar pessoa 
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");//carrega para atela
					modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
					modelAndView.addObject("pessoaobj",new Pessoa());//retorna um objeto varzio
					return modelAndView;		
		}
		
		
		
		// metodo para pesquisar é do formulario de pesquisa 
		@PostMapping("**/pesquisarpessoa") 
		public ModelAndView Pesquisar(@RequestParam ("nomepesquisa") String nomepesquisa,
				@RequestParam ("pesqsexo") String pesqsexo,
				@PageableDefault(size= 5, sort= {"nome"}) Pageable pageable) {
			
			Page<Pessoa> pessoas = null;
			
			if(pesqsexo != null && !pesqsexo.isEmpty()) {
				pessoas = pessoaRepository.findPessoaBySexoPage(nomepesquisa, pesqsexo, pageable);
			}else {
				pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);//fazendo a conslta por nome 
			
			}
			
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //retornando para a tela de cadastro
					modelAndView.addObject("pessoas", pessoas); //fazendo a consulta
					modelAndView.addObject("pessoaobj", new Pessoa());
					modelAndView.addObject("nomepesquisa", nomepesquisa);
					return modelAndView; 
		}
		
		
		
		
		
		// metodo para fazer o download
		@GetMapping("/**baixarcurriculo/{idpessoa}")
		public void baixarcurriculo(@PathVariable("idpessoa")Long idpessoa,
				HttpServletResponse response) throws IOException{
			
			//consultar objeto pessoa no banco de dados 
			Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
			if(pessoa.getCurriculo() !=null) {
				
				//seta tamanho da resposta
				response.setContentLength(pessoa.getCurriculo().length);
				
				//tipo do arquivo para download ou pode ser generica application/octet-stream
				response.setContentType(pessoa.getTipoFileCurriculo());
				
				//define o cabeçalho da reposta 
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
				response.setHeader(headerKey, headerValue);
				
				//finaliza a reposta passando o arquivo
				response.getOutputStream().write(pessoa.getCurriculo());
			}
		}
		
		
		 
		//metodo para download o PDF
		@GetMapping("**/pesquisarpessoa") 
		public void imprimePdf(@RequestParam ("nomepesquisa") String nomepesquisa,
				@RequestParam ("pesqsexo") String pesqsexo,
				HttpServletRequest request, 
				HttpServletResponse response) throws Exception {

			List<Pessoa> pessoas = new ArrayList<Pessoa>();
			
			if(pesqsexo != null  && !pesqsexo.isEmpty()
					&& nomepesquisa != null && !nomepesquisa.isEmpty()) { // busca por nome e sexo			
				pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
				
				
			}else if(nomepesquisa != null && nomepesquisa.isEmpty()){ // busca somente por nome 
				pessoas = pessoaRepository.findPessoaByName(nomepesquisa);			
					
			} 
			
		else if(pesqsexo != null && pesqsexo.isEmpty()){ // busca somente por sexo 
			pessoas = pessoaRepository.findPessoaBySexo(pesqsexo);	 		
				
		}
			
			else { // busca todos
					
					Iterable<Pessoa> iterator = pessoaRepository.findAll();
					for(Pessoa pessoa : iterator) {
						pessoas.add(pessoa); }
				}
				
			// chame o serviço que faz a geracao do relatorio
			byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
					
			// tamanho da resposta 
			response.setContentLength(pdf.length);
			
			//definir na resposta o tipo de arquivo
			response.setContentType("application/octet-stream");
			
			//difinir o cabeçalho da reposta 
			String headerkey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
			response.setHeader(headerkey, headerValue);
			
			// finalizando a resposta para o navegador 
			response.getOutputStream().write(pdf);
			
			}
		 
		
		
		
		// metodo para lista os telefones  
		@GetMapping("/telefones/{idpessoa}")
		public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa){
			
					Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);//carregando do banco
					
					ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
					modelAndView.addObject("pessoaobj", pessoa.get());
					modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
					return modelAndView;		
		}
		
		// metodo para adicionar telefones e fazendo toda a validaçao para nao salvar null e nem vazio
		@PostMapping("**/addfonePessoa/{pessoaid}")
		public ModelAndView addFonePessoa(Telefone telefone, 
						@PathVariable("pessoaid") Long pessoaid) {
			
			Pessoa pessoa = pessoaRepository.findById(pessoaid).get();//consultando pessoa 
			
			// fazendo as validaçoes com Spring
			if(telefone !=null && (telefone.getNumero().isEmpty()) ||telefone.getTipo().isEmpty()){
				
				ModelAndView modelAndView = new ModelAndView("cadastro/telefones");//retornando a mesma tela 
				modelAndView.addObject("pessoaobj", pessoa);
				modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
				
				
				List<String> msg = new ArrayList<String>();
				
				if(telefone.getNumero().isEmpty()) {
				msg.add("O NUMERO deve ser informado");//validaçao da mensagem
				}			
				if(telefone.getTipo().isEmpty()) { //validaçao da mensagem
				msg.add("O TIPO deve sef Informado");
				}
				modelAndView.addObject("msg", msg);
				return modelAndView;
			}
			
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); //retornando a view
			telefone.setPessoa(pessoa);
			
			telefoneRepository.save(telefone);//adicionando pessoa ao telefone
			
			modelAndView.addObject("pessoaobj", pessoa);//adicionando o retorno do objeto pai 
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			return modelAndView;			
		}
		
		
		
		
		// metodo para excluir telefone
		@GetMapping("/removertelefone/{idtelefone}")
		public ModelAndView removertelefone(@PathVariable("idtelefone") Long idtelefone) {

			Pessoa pessoa =  telefoneRepository.findById(idtelefone).get().getPessoa();
			
			telefoneRepository.deleteById(idtelefone);// deletar telefone
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");// carrega para a mesma atela
			modelAndView.addObject("pessoaobj", pessoa); //passanado o objeto pai
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));// retorna um tudo
			return modelAndView;

		}
}
