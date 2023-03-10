package com.universales.practica3.ws;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.universales.practica3.wsint.UsuarioInt;
import com.universales.practica3.dto.UsuarioDTO;
import com.universales.practica3.entity.Usuario;
import com.universales.practica3.repository.UsuarioRepository;
import com.universales.practica3.security.JwtGeneratorInterface;

@Component
public class UsuarioImpl implements UsuarioInt {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	JwtGeneratorInterface jwtGenerator;
	
	@Autowired
	private ModelMapper modelMapper;

	private static final Log LOG = LogFactory.getLog(UsuarioImpl.class);
	
	/**
	 * Devuelve la lista de todos los Usuarios.
	 * 
	 * @return lista de todos los Usuarios.
	 */
	
	@Override
	public List<Usuario> buscarUsuario() {
		return usuarioRepository.findAll();
	}
	
	/**
	 * Guarda un nuevo cliente en la base de datos.
	 * 
	 * @param usuarios objeto UsuarioDTO con los datos del nuevo usuario
	 * @return el usuario guardado
	 */

	@Override
	public Usuario guardar(UsuarioDTO usuarios) {
	Usuario usuario= modelMapper.map(usuarios, Usuario.class);
	return usuarioRepository.save(usuario);
	}
	
	/**
	 * Elimina un usuario y todos sus seguros asociados de la base de datos.
	 * 
	 * @param idUsuario el id del usuario a eliminar
	 */

	@Override
	public void eliminar(Integer idUsuario) {
		Optional<Usuario> usuarios = usuarioRepository.findById(idUsuario);
		if (usuarios.isPresent()) {
			usuarioRepository.delete(usuarios.get());
		}
	}
	
	/**
	 * Busca un usuario por medio de su id
	 * 
	 * @param idUsuario el id del usuario a buscar
	 * @return una lista de usuarios que contienen el id buscado
	 */

	@Override
	public List<Usuario> buscarPorIdUsuario(Integer idUsuario) {
		return usuarioRepository.findByidUsuario(idUsuario);
	}
	
	/**
	 * Realiza la autenticaci??n de un usuario por medio de su usuario y contrase??a y genera un token de acceso
	 * 
	 * @param usuario el usuario a autenticar
	 * @return un objeto de tipo ResponseEntity con el token generado y el estado HTTP
	 */
	
	@Override
	public ResponseEntity<Object> loginUser(@RequestBody Usuario usuario) {
		try {
			if (usuario.getUser() == null || usuario.getContrasena() == null) {
				throw new UserPrincipalNotFoundException("Usuario o contrase??a vacio");
			}
			Usuario userData = getUsuarioByUsuarioAndContrasena(usuario.getUser(), usuario.getContrasena());
			if (userData == null) {
				throw new UserPrincipalNotFoundException("Usuario o contrase??a incorrectos");
			}
			return new ResponseEntity<>( jwtGenerator.generateToken(usuario), HttpStatus.OK);
		} catch (UserPrincipalNotFoundException e) {
			LOG.error(e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}
	
	/**
	*
	*Busca un usuario en la base de datos que coincida con el nombre de usuario y la contrase??a proporcionados.
	* @param usuario el nombre del usuario a buscar.
	* @param contrasena la contrase??a del usuario a buscar.
	* @return un objeto Usuario que corresponde al usuario encontrado.
	* @throws UserPrincipalNotFoundException si no se encuentra ning??n usuario que coincida con los par??metros proporcionados.
	*/
	
	@Override
	public Usuario getUsuarioByUsuarioAndContrasena(String usuario,String contrasena) throws UserPrincipalNotFoundException {
	Usuario user= usuarioRepository.findByUserAndContrasena(usuario, contrasena);
	if(user==null) {
		throw new UserPrincipalNotFoundException("Usuario o password invalido");
	}
	return user;
		
		
	}
}
