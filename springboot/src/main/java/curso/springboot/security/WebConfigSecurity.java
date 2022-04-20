package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserdDatailsService implementacaoUserdDatailsService;
	
	@Override //configura as solicitações de acesso por HTTP
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf()
		.disable()//desativa as configuraçoes padrão de memória.
		.authorizeRequests() //permite restringir acessos.
		.antMatchers(HttpMethod.GET, "/").permitAll() // qualquer usuario acessa a pagina inicial.
		.antMatchers("/materialize/**").permitAll()
		.antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("ADMIN") //so acessa a pagina agora quem é admin
		.anyRequest().authenticated()
		.and().formLogin().permitAll()//permite qualquer usuario
		.loginPage("/login") //vai direcionar para essa pagina html
		.defaultSuccessUrl("/cadastropessoa")//quando logar vai direcionar para essa pagina
		.failureUrl("/login?error=true")// se falhar redireciona para a mesma tela de login e passando um parametro de erro 
		.and().logout().logoutSuccessUrl("/login") // mapeia URl de Logout e invalida usuario autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	} 
	
	@Override // cria autenticaçao do usuario com o banco ou memória/validacao em memoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		
		auth.userDetailsService(implementacaoUserdDatailsService) 
		.passwordEncoder(new BCryptPasswordEncoder()); 
		
		
		//aqui era a autenticaçao em memoria
//		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
//		.withUser("lucas")
//		.password("$2a$10$8cWwi/nmtHfcExBuqpdFyOBeISFLMB6/CcTGihDuxDkTeaZZxYJNe") // senha 123 criptografada
//		.roles("ADMIN");
		 
	}
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
			// SE OCORRER AGUM ERRO TIRA TODAS ESSAS FUNCOES COMENTADADA 
	          web.ignoring(); //.antMatchers("/materialize/**")

	         //.antMatchers(HttpMethod.GET,"/resources/**","/static/**", "/**", "/materialize/**", "**/materialize/**");
	}
}
