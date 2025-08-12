package com.example.lms.util;

import com.example.lms.dto.book.BookResponseDTO;
import com.example.lms.model.Book;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Book.class, BookResponseDTO.class).addMappings(mapper ->
                mapper.map(src -> src.getAuthor().getId(), BookResponseDTO::setAuthorId)
        );

        return modelMapper;
    }
}