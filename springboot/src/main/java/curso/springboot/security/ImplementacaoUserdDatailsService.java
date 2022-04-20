package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import curso.springboot.model.Usuario;
import curso.springboot.repository.UsuarioRepository;

//esse service ela vai usar a interface UserDetailsService que vai ser necessaria para o security

@Service
@Transactional
public class ImplementacaoUserdDatailsService implements UserDetailsService {

	//fazendo a injecao de dependencia 
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(usuario == null) {
			throw new  UsernameNotFoundException("Usuario nao foi encontrado");
			
		}
		
		return new User(usuario.getLogin(),usuario.getPassword(), usuario.isEnabled(), 
				true, true, true, usuario.getAuthorities()); //vai carregar as autoridades carregadas na tabela 
	}

}
