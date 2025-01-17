package com.github.victorlaranjeira.clientes.rest;

import com.github.victorlaranjeira.clientes.model.entity.Cliente;
import com.github.victorlaranjeira.clientes.model.entity.ServicoPrestado;
import com.github.victorlaranjeira.clientes.model.repository.ClienteRepository;
import com.github.victorlaranjeira.clientes.model.repository.ServicoPrestadoRepository;
import com.github.victorlaranjeira.clientes.rest.dto.ServicoPrestadoDTO;
import com.github.victorlaranjeira.clientes.util.BigDecimalConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/servicos-prestados")
@RequiredArgsConstructor // Cria o construtor com os argumentos obrigatórios marcados com final
public class ServicoPrestadoController {

    private final ClienteRepository clienteRepository;
    private final ServicoPrestadoRepository repository;
    private final BigDecimalConverter bigDecimalConverter;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoPrestado salvar( @RequestBody ServicoPrestadoDTO dto ) {
        LocalDate data = LocalDate.parse(dto.getData(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Integer id_cliente = dto.getId_cliente();

        Cliente cliente = clienteRepository
                .findById(id_cliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente inexistente."));

        ServicoPrestado servicoPrestado = new ServicoPrestado();
        servicoPrestado.setDescricao(dto.getDescricao());
        servicoPrestado.setData( data );
        servicoPrestado.setCliente( cliente );
        servicoPrestado.setValor( bigDecimalConverter.converter(dto.getPreco()) );

        return repository.save( servicoPrestado );
    }

    @GetMapping
    public List<ServicoPrestado> pesquisar(
            @RequestParam(value = "nome", required = false, defaultValue = "") String nome,
            @RequestParam(value = "mes", required = false) Integer mes
    ) {
        return repository.findByNomeClienteAndMes("%" + nome + "%", mes);
    }

}
