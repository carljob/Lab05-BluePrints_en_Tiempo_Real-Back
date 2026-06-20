package com.eci.blueprints.rt;

import com.eci.blueprints.rt.dto.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = "http://localhost:5173")
public class BlueprintController {

  private final SimpMessagingTemplate template;

  public BlueprintController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @MessageMapping("/draw")
  public void onDraw(DrawEvent evt) {
    var upd = new BlueprintUpdate(evt.author(), evt.name(), List.of(evt.point()));
    template.convertAndSend("/topic/blueprints." + evt.author() + "." + evt.name(), upd);
  }

  @MessageMapping("/clear")
  public void onClear(DrawEvent evt) {
    // Reenvía una señal de limpiar a todos los suscriptores del plano
    var upd = new BlueprintUpdate(evt.author(), evt.name(), null);
    template.convertAndSend("/topic/blueprints." + evt.author() + "." + evt.name() + ".clear", upd);
  }

  @ResponseBody
  @GetMapping("/api/blueprints/{author}/{name}")
  public BlueprintUpdate get(@PathVariable String author, @PathVariable String name) {
    return new BlueprintUpdate(author, name, List.of(new Point(10,10,"#1e40af"), new Point(40,50,"#1e40af")));
  }
}
