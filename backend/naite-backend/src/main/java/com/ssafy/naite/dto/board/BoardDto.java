package com.ssafy.naite.dto.board;

import com.ssafy.naite.domain.board.Board;
import com.ssafy.naite.domain.like.BoardLike;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BoardDto {

    /**
     * insert board dto
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BoardSaveRequestDto {
        private int bigCategoryNo;
        private String boardTitle;
        private String boardContent;
        private String boardPic;
        private int unknownFlag;
        private int openFlag;

        @Builder
        public BoardSaveRequestDto(int userNo, int bigCategoryNo, String boardTitle, String boardContent, String boardPic, int unknownFlag, int openFlag) {
            this.bigCategoryNo = bigCategoryNo;
            this.boardTitle = boardTitle;
            this.boardContent = boardContent;
            this.boardPic = boardPic;
            this.unknownFlag = unknownFlag;
            this.openFlag = openFlag;
        }

        public Board toEntity(int userNo) {
            return Board.builder()
                    .userNo(userNo)
                    .bigCategoryNo(bigCategoryNo)
                    .boardTitle(boardTitle)
                    .boardContent(boardContent)
                    .boardPic(boardPic)
                    .boardCreatedAt(LocalDateTime.now())
                    .boardUpdatedAt(LocalDateTime.now())
                    .unknownFlag(unknownFlag)
                    .openFlag(openFlag)
                    .build();
        }
    }

    /**
     * update board dto
     */
    @Getter
    @NoArgsConstructor
    public static class BoardUpdateRequestDto {
        private String boardTitle;
        private String boardContent;
        private String boardPic;
        private int unknownFlag;
        private int openFlag;

        @Builder
        public BoardUpdateRequestDto(String boardTitle, String boardContent, String boardPic, int unknownFlag, int openFlag) {
            this.boardTitle = boardTitle;
            this.boardContent = boardContent;
            this.boardPic = boardPic;
            this.unknownFlag = unknownFlag;
            this.openFlag = openFlag;
        }
    }

    /**
     * select board dto
     */
    @Getter
    @Setter
    public static class BoardResponseDto {

        private int boardNo;
        private int userNo;
        private int bigCategoryNo;
        private String boardTitle;
        private String boardContent;
        private String boardCreatedAt;
        private String boardUpdatedAt;
        private int boardLikeCnt;
        private int unknownFlag;
        private int boardReportCnt;
        private int openFlag;
        private int boardIsDeleted;
        private List<Integer> usersWithLike = new ArrayList<Integer>();
        private String userName;

        public BoardResponseDto(Board board) {
            this.boardNo = board.getBoardNo();
            this.userNo = board.getUserNo();
            this.bigCategoryNo = board.getBigCategoryNo();
            this.boardTitle = board.getBoardTitle();
            this.boardContent = board.getBoardContent();
            this.boardCreatedAt = board.getBoardCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm (E)"));
            this.boardUpdatedAt = board.getBoardUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm (E)"));
            this.boardLikeCnt = board.getBoardLikeCnt();
            this.unknownFlag = board.getUnknownFlag();
            this.boardReportCnt = board.getBoardReportCnt();
            this.openFlag = board.getOpenFlag();
            this.boardIsDeleted = board.getBoardIsDeleted();
        }
    }

    /**
     * add like to board
     */
    @Getter
    @NoArgsConstructor
    public static class LikeRequestSaveDto {
        private int boardNo;

        @Builder
        LikeRequestSaveDto(int boardNo) {
            this.boardNo = boardNo;
        }

        public BoardLike toEntity(int userNo) {
            return BoardLike.builder()
                    .userNo(userNo)
                    .boardNo(boardNo)
                    .build();
        }
    }

    @Setter
    @Getter
    public static class LikeResponseDto {
        private int userNo;
        private int boardNo;

        public LikeResponseDto(BoardLike boardLike) {
            this.userNo = boardLike.getUserNo();
            this.boardNo = boardLike.getBoardNo();
        }
    }
}
