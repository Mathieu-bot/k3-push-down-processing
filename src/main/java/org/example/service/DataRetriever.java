package org.example.service;

import org.example.database.DBConnection;
import org.example.model.CarSalesRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Integer> getPiecesVenduesParMarqueLigneUnique() {
        String sql = """
            SELECT
                COALESCE(SUM(CASE WHEN c.marque = 'KIA' THEN p.quantite ELSE 0 END), 0) AS nbre_piece_kia,
                COALESCE(SUM(CASE WHEN c.marque = 'DAEWOO' THEN p.quantite ELSE 0 END), 0) AS nbre_piece_daewoo
            FROM voiture c
            LEFT JOIN piece p ON c.id = p.voiture_id
            """;

        Map<String, Integer> results = new HashMap<>();

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery())
        {
            if (rs.next()) {
                results.put("nbre_piece_kia", rs.getInt("nbre_piece_kia"));
                results.put("nbre_piece_daewoo", rs.getInt("nbre_piece_daewoo"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    public Map<String, Double> getPourcentageVenteParMarqueAvant2026() {
        String sql = """
            SELECT
                c.marque,
                ROUND(
                    COALESCE(SUM(p.quantite), 0) * 100.0 / NULLIF(
                        (SELECT COALESCE(SUM(p2.quantite), 0)
                         FROM piece p2
                         JOIN voiture c2 ON p2.voiture_id = c2.id
                         WHERE p2.date_vente < '2026-01-01'), 0
                    ), 2
                ) AS pourcentage
            FROM voiture c
            LEFT JOIN piece p ON c.id = p.voiture_id AND p.date_vente < '2026-01-01'
            GROUP BY c.marque
            ORDER BY c.marque
            """;

        Map<String, Double> results = new HashMap<>();

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery())
        {
            while (rs.next()) {
                String marque = rs.getString("marque");
                double pourcentage = rs.getDouble("pourcentage");
                results.put(marque, pourcentage);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

}

