package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.ChatUserDto;
import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.payload.request.RegisterUserRequest;
import com.simple_cabinet_medical.Backend.service.UtilisateurService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<UtilisateurDto> changeStatus(@PathVariable Long id) {
        return new ResponseEntity<>(utilisateurService.changeStatus(id), HttpStatus.OK);
    }

    @PreAuthorize("@authz.hasDeleteCustomPermission(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        utilisateurService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@PostFilter("@authz.hasReadCustomPermission(filterObject)")
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDto> getUtilisateur(@PathVariable Long id) {
        return new ResponseEntity<>(utilisateurService.getUtilisateur(id), HttpStatus.OK);
    }

    @PostFilter("@authz.hasReadCustomPermission(filterObject)")
    @GetMapping
    public ResponseEntity<Page<UtilisateurDto>> getAllUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(utilisateurService.getAllUtilisateurs(page, size), HttpStatus.OK);
    }

    @PostFilter("@authz.hasReadCustomPermission(filterObject)")
    @GetMapping("/search/by-client")
    public ResponseEntity<Page<UtilisateurDto>> getAllUtilisateursByClient(
            @RequestParam Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(utilisateurService.getAllUtilisateursByClient(clientId, page, size), HttpStatus.OK);
    }

    @PreAuthorize("@authz.hasUpdateCustomPermission(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDto> updateUser(@PathVariable Long id, @RequestBody RegisterUserRequest request) {
        UtilisateurDto updatedUser = utilisateurService.updateUser(id, request);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("user-chat-info")
    public ResponseEntity<ChatUserDto> getUserInfoforChat(
            @AuthenticationPrincipal Utilisateur utilisateur) {

        ChatUserDto chatUserDto =
                utilisateurService.getUserInfoforChat(utilisateur.getNomUtilisateur());

        return ResponseEntity.ok(chatUserDto);
    }
}
