package com.inspired.azurecomparison.service.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataDynamicService {

    private final EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(DataDynamicService.class);

    public List<String> getAllData(String tableName) {
        logger.debug("Reading data from table {}", tableName );
        String query = "SELECT * FROM " + tableName;
        Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> databaseResult = nativeQuery.getResultList();
        return databaseResult.stream().map(objects -> Arrays.stream(objects)
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.toList());
    }
}
