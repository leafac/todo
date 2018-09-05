package edu.jhu.cs.pl.to_do.repositories;

import edu.jhu.cs.pl.to_do.models.Item;
import org.postgresql.ds.PGSimpleDataSource;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemsRepository {
    private DataSource database;

    public ItemsRepository(DataSource database) throws SQLException {
        this.database = database;
        var connection = database.getConnection();
        var statement = connection.createStatement();
        if (database instanceof SQLiteDataSource) {
            statement.execute("CREATE TABLE IF NOT EXISTS items (id integer PRIMARY KEY AUTOINCREMENT, description text NOT NULL)");
        }
        else if (database instanceof PGSimpleDataSource) {
            statement.execute("CREATE TABLE IF NOT EXISTS items (id SERIAL PRIMARY KEY, description text NOT NULL)");
        }
        statement.close();
        connection.close();
    }

    public void create(Item item) throws SQLException {
        var connection = database.getConnection();
        var statement = connection.prepareStatement("INSERT INTO items (description) VALUES (?)");
        statement.setString(1, item.getDescription());
        statement.executeUpdate();
        statement.close();
        connection.close();
    }

    public void update(Item item) throws SQLException {
        var connection = database.getConnection();
        var statement = connection.prepareStatement("UPDATE items SET description = ? WHERE id = ?");
        statement.setString(1, item.getDescription());
        statement.setInt(2, item.getId());
        statement.executeUpdate();
        statement.close();
        connection.close();
    }

    public List<Item> getItems() throws SQLException {
        var items = new ArrayList<Item>();
        var connection = database.getConnection();
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT id, description FROM items ORDER BY id");
        while (result.next()) items.add(new Item(result.getInt("id"), result.getString("description")));
        result.close();
        statement.close();
        connection.close();
        return items;
    }

    public Item getItem(int itemIdentifier) throws SQLException, NonExistingItemException {
        var connection = database.getConnection();
        var statement = connection.prepareStatement("SELECT id, description FROM items WHERE id = ?");
        statement.setInt(1, itemIdentifier);
        var result = statement.executeQuery();
        if (! result.next()) throw new NonExistingItemException();
        var item = new Item(result.getInt("id"), result.getString("description"));
        result.close();
        statement.close();
        connection.close();
        return item;
    }

    public void deleteItem(Item item) throws SQLException {
        var connection = database.getConnection();
        var statement = connection.prepareStatement("DELETE FROM items WHERE id = ?");
        statement.setInt(1, item.getId());
        statement.executeUpdate();
        statement.close();
        connection.close();
    }

    public static class NonExistingItemException extends Exception {}
}
