package curso.springboot.model;
// essa class significa role/papel do spring security
//vai virar uma tabela no banco e o spring vai carregar os nomes do acesso 

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String nomeRole;
	
	
	@Override
	public String getAuthority() {// vai retorna  ROLE_ADMIN, ROLE_GERENTE, ROLE_SECRETARIA
		return this.nomeRole;
	}


	public String getNomeRole() {
		return nomeRole;
	}


	public void setNomeRole(String nomeRole) {
		this.nomeRole = nomeRole;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	
	
}
