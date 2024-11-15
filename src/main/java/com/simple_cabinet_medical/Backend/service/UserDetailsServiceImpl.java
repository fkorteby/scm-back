package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import java.util.Collection;

import com.simple_cabinet_medical.Backend.permisson.Utils;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    @Transactional
    @Override
    public UserDetails loadUserByUsername (String nomUtilisateur) throws UsernameNotFoundException {
        Utilisateur user = utilisateurRepository.findByNomUtilisateur(nomUtilisateur).get();


        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + nomUtilisateur);
        }


        if(user.getStatus().equals("Disabled")) {
            throw new UsernameNotFoundException("User disabled");
        }

        if(user.getType().equals("CLIENT")) {
            if(user.getClient().getStatus().equals("Disabled")) {
                throw new UsernameNotFoundException("Client disabled");
            }
        }


        return new org.springframework.security.core.userdetails.User(user.getNomUtilisateur(), user.getMdp(),
                getAuthorities(user));
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Utilisateur user) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();


        // add the global Authority
        authorities.add(new SimpleGrantedAuthority(user.getType()));


        // add the client Authority
        if (user.getClient() != null) {
            authorities.add(new SimpleGrantedAuthority(Utils.CLIENT_AUTH_PREFIX + user.getClient().getIdClient()));
        }


        return authorities;
    }

}
