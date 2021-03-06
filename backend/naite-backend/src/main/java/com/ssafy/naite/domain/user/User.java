package com.ssafy.naite.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "user_no")
    @Id
    private Integer userNo;

    @Column(length = 15, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(nullable = false)
    private String userPw;

    private String userSalt;

    @Column(nullable = false)
    private String userBasicAddress;

    private String userDetailAddress;

    @Column(nullable = false)
    private String userNick;

    @Column(nullable = false)
    private Integer userScore;

    private Integer userReportCnt;
    private String userPic;
    private Byte userActive;

    public User(int userNo) {
        this.userNo = userNo;
    }

    @Builder
    public User(String userId, String userName, String userEmail, String userPw, String userSalt, String userBasicAddress, String userDetailAddress, String userNick, String userPic) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.userBasicAddress = userBasicAddress;
        this.userDetailAddress = userDetailAddress;
        this.userNick = userNick;
        this.userPic = userPic;
        this.userReportCnt = 0;
        this.userActive = 1;
        this.userScore = 42;
        this.userSalt = userSalt;
    }

    public void updateScoreAfterMarket(int newScore) {
        this.userScore = newScore;
    }

    public void updateScore(){
        if (this.userScore > 0)
            this.userScore -= 1;
    }

    public void leave(Byte userSecession) {
        this.userActive = userSecession;
    }

    public void updateAddress(String userBasicAddress, String userDetailAddress){
        this.userBasicAddress = userBasicAddress;
        this.userDetailAddress = userDetailAddress;
    }

    public void updateNick(String newNick) {
        this.userNick = newNick;
    }

    public void updateUserPic(String userPic) {
        this.userPic = userPic;
    }
}
