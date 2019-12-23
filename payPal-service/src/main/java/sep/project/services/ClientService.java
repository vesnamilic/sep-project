package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		
		if(getClient(client.getEmail()) == null && client.getId() == null) {
			Client saved = clientRepository.save(client);
					
			return saved;
		}
				
		return null;
	}

}
