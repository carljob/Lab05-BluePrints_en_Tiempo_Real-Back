package com.eci.blueprints.rt;

import com.eci.blueprints.rt.dto.BlueprintUpdate;
import com.eci.blueprints.rt.dto.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacén en memoria de blueprints.
 * La clave es "autor/nombre" y el valor es la lista de puntos.
 * Es un componente único (singleton) compartido por los controladores REST y STOMP.
 */
@Component
public class BlueprintStore {

    private final Map<String, List<Point>> store = new ConcurrentHashMap<>();

    private String key(String author, String name) {
        return author + "/" + name;
    }

    public List<BlueprintUpdate> listByAuthor(String author) {
        List<BlueprintUpdate> result = new ArrayList<>();
        store.forEach((k, pts) -> {
            String[] parts = k.split("/", 2);
            if (parts.length == 2 && parts[0].equals(author)) {
                result.add(new BlueprintUpdate(author, parts[1], pts));
            }
        });
        return result;
    }

    public BlueprintUpdate get(String author, String name) {
        List<Point> pts = store.getOrDefault(key(author, name), new ArrayList<>());
        return new BlueprintUpdate(author, name, pts);
    }

    public boolean create(String author, String name) {
        return store.putIfAbsent(key(author, name), new ArrayList<>()) == null;
    }

    public void update(String author, String name, List<Point> points) {
        store.put(key(author, name), new ArrayList<>(points));
    }

    public void addPoint(String author, String name, Point point) {
        store.computeIfAbsent(key(author, name), k -> new ArrayList<>()).add(point);
    }

    public void delete(String author, String name) {
        store.remove(key(author, name));
    }
}