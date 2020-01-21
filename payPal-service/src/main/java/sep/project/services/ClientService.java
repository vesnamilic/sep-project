package sep.project.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.dto.FieldDTO;
import sep.project.dto.FieldType;
import sep.project.model.Client;
import sep.project.repositories.ClientRepository;

@Service
public class ClientService {
	
	@Autowired
	ClientRepository clientRepository;
		
	public Client getClient(String email) {
		
		return clientRepository.findByEmail(email);
	}
	
	public Client save(Client client) {
		
		return clientRepository.save(client);			
	}

	public List<FieldDTO> getFields(){
		
		List<FieldDTO> list = new ArrayList<FieldDTO>();
		
		list.add(new FieldDTO("clientID", "Client ID", FieldType.TEXT, true));
		list.add(new FieldDTO("clientSecret", "Secret", FieldType.TEXT, true));
		
		return list;
	}
}
