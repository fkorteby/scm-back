//package com.simple_cabinet_medical.Backend.interceptor;
//
//import com.simple_cabinet_medical.Backend.config.webSocket.StompPrincipal;
//import com.simple_cabinet_medical.Backend.model.Utilisateur;
//import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
//import com.simple_cabinet_medical.Backend.service.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//
//import java.security.Principal;
//
//@Component
//@RequiredArgsConstructor
//public class JwtStompAuthInterceptor implements ChannelInterceptor {
//
//    private final JwtService jwtService;
//    private final UtilisateurRepository utilisateurRepository;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
//
//            String authHeader = accessor.getFirstNativeHeader("Authorization");
//
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                throw new AccessDeniedException("Token STOMP manquant");
//            }
//
//            String token = authHeader.substring(7);
//            String username = jwtService.extractNomUtilisateur(token);
//
//            Utilisateur user = utilisateurRepository.findByNomUtilisateur(username)
//                    .orElseThrow(() -> new AccessDeniedException("Utilisateur introuvable"));
//
//            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//
//            if (!jwtService.isTokenValid(token,userDetails)) {
//                throw new AccessDeniedException("Token STOMP invalide");
//            }
//
//            // Le "name" du Principal devient l'identifiant utilisé par
//            // convertAndSendToUser côté serveur — on utilise l'id utilisateur.
//            Principal principal = new StompPrincipal(String.valueOf(user.getIdUtilisateur()));
//            accessor.setUser(principal);
//        }
//
//        return message;
//    }
//}