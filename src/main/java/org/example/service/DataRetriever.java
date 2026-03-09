package org.example.service;

import org.example.database.DBConnection;
import org.example.model.CarSalesRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    public List<CarSalesRecord> getPiecesVenduesParMarqueModele() {
        String sql = """
            SELECT c.marque, c.modele, COALESCE(SUM(p.quantite), 0) AS nbre_pieces_vendus
            FROM voiture c
            LEFT JOIN piece p ON c.id = p.voiture_id
            GROUP BY c.id, c.marque, c.modele
            """;

        List<CarSalesRecord> results = new ArrayList<>();

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery())
        {
            while (rs.next()) {
                String marque = rs.getString("marque");
                String modele = rs.getString("modele");
                int nbrePiecesVendus = rs.getInt("nbre_pieces_vendus");

                results.add(new CarSalesRecord(marque, modele, nbrePiecesVendus));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

}

