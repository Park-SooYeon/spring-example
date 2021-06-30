package com.example.logindemo.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/api/member")
    public String insertMember(@RequestBody Member member) {
        String pwd = passwordEncoder.encode(member.getPassword());
        member.setPassword(pwd);

        memberRepository.save(member);
        return "success";
    }
}
