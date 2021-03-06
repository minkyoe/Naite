package com.ssafy.naite.service.board;

import com.ssafy.naite.domain.board.Board;
import com.ssafy.naite.domain.board.BoardRepository;
import com.ssafy.naite.domain.comment.CommentRepository;
import com.ssafy.naite.domain.like.LikePK;
import com.ssafy.naite.domain.like.LikeRepository;
import com.ssafy.naite.domain.market.Market;
import com.ssafy.naite.domain.market.MarketRepository;
import com.ssafy.naite.domain.picture.Picture;
import com.ssafy.naite.domain.picture.PictureRepository;
import com.ssafy.naite.domain.review.Review;
import com.ssafy.naite.domain.review.ReviewRepository;
import com.ssafy.naite.domain.user.User;
import com.ssafy.naite.domain.user.UserRepository;
import com.ssafy.naite.domain.village.Village;
import com.ssafy.naite.domain.village.VillageRepository;
import com.ssafy.naite.dto.board.BoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PictureRepository pictureRepository;
    private final VillageRepository villageRepository;
    private final ReviewRepository reviewRepository;
    private final MarketRepository marketRepository;

    /**
     * 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public List<BoardDto.BoardResponseDto> findAllBoards(int userNo) {
        String userVillageName = villageRepository.findByUserNo(userNo).orElseThrow(() -> new IllegalAccessError("주소가 없어요ㅜㅜ. 잘못된 계정입니다!")).getVillageName();
        return boardRepository.findAll()
                .stream()
                .filter(board -> board.getBoardIsDeleted() == 0)
//                .filter(board -> villageRepository.findByUserNo(board.getUserNo()).orElse(new Village(userNo, userVillageName)).getVillageName().equals(userVillageName))
                .sorted(Comparator.comparing(Board::getBoardCreatedAt).reversed())
                .map(BoardDto.BoardResponseDto::new)
                .map(boardResponseDto -> {
                    User user = userRepository.findById(boardResponseDto.getUserNo()).get();
                    boardResponseDto.setUserNick(user.getUserNick());
                    boardResponseDto.setUserPic(user.getUserPic());
                    boardResponseDto.setBoardCommentCnt(commentRepository.findAll().stream().filter(comment -> comment.getBoard().getBoardNo() == boardResponseDto.getBoardNo()).collect(Collectors.toList()).size());
                    boardResponseDto.setFiles(pictureRepository.findPicByBoardNo(boardResponseDto.getBoardNo()));
                    return boardResponseDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 카테고리별 조회
     */
    @Transactional(readOnly = true)
    public List<BoardDto.BoardResponseDto> findAllBoardsByCategory(int bigCategoryNo, int userNo) {
        String userVillageName = villageRepository.findByUserNo(userNo).orElseThrow(() -> new IllegalAccessError("주소가 없어요ㅜㅜ. 잘못된 계정입니다!")).getVillageName();
        return boardRepository.findAll()
                .stream()
                .filter(board -> board.getBoardIsDeleted() == 0)
                .filter(board -> board.getBigCategoryNo() == bigCategoryNo)
//                .filter(board -> villageRepository.findByUserNo(board.getUserNo()).orElse(new Village(userNo, userVillageName)).getVillageName().equals(userVillageName))
                .sorted(Comparator.comparing(Board::getBoardCreatedAt).reversed())
                .map(BoardDto.BoardResponseDto::new)
                .map(boardResponseDto -> {
                    User user = userRepository.findById(boardResponseDto.getUserNo()).get();
                    boardResponseDto.setUserNick(user.getUserNick());
                    boardResponseDto.setUserPic(user.getUserPic());
                    boardResponseDto.setBoardCommentCnt(commentRepository.findAll().stream().filter(comment -> comment.getBoard().getBoardNo() == boardResponseDto.getBoardNo()).collect(Collectors.toList()).size());
                    boardResponseDto.setFiles(pictureRepository.findPicByBoardNo(boardResponseDto.getBoardNo()));
                    return boardResponseDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 좋아요 높은 순서로 5개
     */
    @Transactional(readOnly = true)
    public List<BoardDto.BoardResponseDto> findTopLikedBoardsByCategory(int bigCategoryNo, int userNo) {
        String userVillageName = villageRepository.findByUserNo(userNo).orElseThrow(() -> new IllegalAccessError("주소가 없어요ㅜㅜ. 잘못된 계정입니다!")).getVillageName();
        List<BoardDto.BoardResponseDto> boardResponseDtoList = boardRepository.findAll()
                                                                            .stream()
                                                                            .filter(board -> board.getBoardIsDeleted() == 0)
                                                                            .filter(board -> board.getBigCategoryNo() == bigCategoryNo)
//                                                                            .filter(board -> villageRepository.findByUserNo(board.getUserNo()).orElse(new Village(userNo, userVillageName)).getVillageName().equals(userVillageName))
                                                                            .sorted(Comparator.comparingInt(Board::getBoardLikeCnt).reversed())
                                                                            .map(BoardDto.BoardResponseDto::new)
                                                                            .map(boardResponseDto -> {
                                                                                User user = userRepository.findById(boardResponseDto.getUserNo()).get();
                                                                                boardResponseDto.setUserNick(user.getUserNick());
                                                                                boardResponseDto.setUserPic(user.getUserPic());
                                                                                boardResponseDto.setBoardCommentCnt(commentRepository.findAll().stream().filter(comment -> comment.getBoard().getBoardNo() == boardResponseDto.getBoardNo()).collect(Collectors.toList()).size());
                                                                                boardResponseDto.setFiles(pictureRepository.findPicByBoardNo(boardResponseDto.getBoardNo()));
                                                                                return boardResponseDto;
                                                                            })
                                                                            .collect(Collectors.toList());
        int index = boardResponseDtoList.size();
        if(index > 6) {
            index = 6;
        }
        return boardResponseDtoList.subList(0,index);
    }

    /**
     * 게시글 유저별 조회
     */
    @Transactional(readOnly = true)
    public List<BoardDto.BoardResponseDto> findAllBoardsByUserNo(int userNo, int myUserNo) {
        List<Board> boardList = boardRepository.findAll().stream().collect(Collectors.toList());
        if(userNo != myUserNo){
            boardList = boardList.stream().filter(board -> board.getUnknownFlag() == 0).collect(Collectors.toList());
        }
        return boardList
                .stream()
                .filter(board -> board.getBoardIsDeleted() == 0)
                .filter(board -> board.getUserNo() == userNo)
                .filter(board -> board.getBigCategoryNo() != 5)
                .sorted(Comparator.comparing(Board::getBoardCreatedAt).reversed())
                .map(BoardDto.BoardResponseDto::new)
                .map(boardResponseDto -> {
                    User user = userRepository.findById(boardResponseDto.getUserNo()).get();
                    boardResponseDto.setUserNick(user.getUserNick());
                    boardResponseDto.setUserPic(user.getUserPic());
                    boardResponseDto.setBoardCommentCnt(commentRepository.findAll().stream().filter(comment -> comment.getBoard().getBoardNo() == boardResponseDto.getBoardNo()).collect(Collectors.toList()).size());
                    boardResponseDto.setFiles(pictureRepository.findPicByBoardNo(boardResponseDto.getBoardNo()));
                    return boardResponseDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 제목으로 조회
     */
    @Transactional(readOnly = true)
    public List<BoardDto.BoardResponseDto> findAllBoardsByTitle(String boardTitle, int userNo) {
        String userVillageName = villageRepository.findByUserNo(userNo).orElseThrow(() -> new IllegalAccessError("주소가 없어요ㅜㅜ. 잘못된 계정입니다!")).getVillageName();
        return boardRepository.findAll()
                .stream()
                .filter(board -> board.getBoardIsDeleted() == 0)
                .filter(board -> board.getBoardTitle().contains(boardTitle))
//                .filter(board -> villageRepository.findByUserNo(board.getUserNo()).orElse(new Village(userNo, userVillageName)).getVillageName().equals(userVillageName))
                .sorted(Comparator.comparing(Board::getBoardCreatedAt).reversed())
                .map(BoardDto.BoardResponseDto::new)
                .map(boardResponseDto -> {
                    User user = userRepository.findById(boardResponseDto.getUserNo()).get();
                    boardResponseDto.setUserNick(user.getUserNick());
                    boardResponseDto.setUserPic(user.getUserPic());
                    boardResponseDto.setBoardCommentCnt(commentRepository.findAll().stream().filter(comment -> comment.getBoard().getBoardNo() == boardResponseDto.getBoardNo()).collect(Collectors.toList()).size());
                    boardResponseDto.setFiles(pictureRepository.findPicByBoardNo(boardResponseDto.getBoardNo()));
                    if(boardResponseDto.getBigCategoryNo() == 3){
                        Board board = boardRepository.findById(boardResponseDto.getBoardNo()).get();
                        Review review = reviewRepository.findReviewByBoard(board).get();
                        boardResponseDto.setReviewNo(review.getReviewNo());
                        boardResponseDto.setReviewStar(review.getReviewStar());
                        boardResponseDto.setSmallCategoryNo(review.getSmallCategoryNo());
                    }
                    if(boardResponseDto.getBigCategoryNo() == 5){
                        Board board = boardRepository.findById(boardResponseDto.getBoardNo()).get();
                        Market market = marketRepository.findMarketByBoard(board).get();
                        boardResponseDto.setMarketNo(market.getMarketNo());
                        boardResponseDto.setMarketCost(NumberFormat.getInstance().format(market.getMarketCost()));
                        boardResponseDto.setMarketIsCompleted(market.getMarketIsCompleted());
                        boardResponseDto.setSmallCategoryNo(market.getSmallCategoryNo());
                    }
                    return boardResponseDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public BoardDto.BoardResponseDto findBoardById(int boardNo) {
        Board board = boardRepository.findById(boardNo).orElseThrow(() -> new IllegalAccessError("[board_no=" + boardNo + "] 해당 게시글이 존재하지 않습니다."));
        BoardDto.BoardResponseDto boardResponseDto = new BoardDto.BoardResponseDto(board);
        User user = userRepository.findById(boardResponseDto.getUserNo()).get();
        boardResponseDto.setUserNick(user.getUserNick());
        boardResponseDto.setUserPic(user.getUserPic());
        boardResponseDto.setUsersWithLike(findAllLikesByBoardNo(boardNo));
        boardResponseDto.setBoardCommentCnt(commentRepository.findAll().stream().filter(comment -> comment.getBoard().getBoardNo() == boardResponseDto.getBoardNo()).collect(Collectors.toList()).size());
        boardResponseDto.setFiles(pictureRepository.findPicByBoardNo(boardResponseDto.getBoardNo()));
        return boardResponseDto;
    }

    /**
     * 게시글 등록
     */
    @Transactional
    public int insertBoard(BoardDto.BoardSaveRequestDto boardSaveRequestDto, int userNo) throws IOException {
        int insertedBoardNo = boardRepository.save(boardSaveRequestDto.toEntity(userNo)).getBoardNo();
        if(boardSaveRequestDto.getFiles() != null) {
            String rootPath = "/home/ubuntu/images/board/";
            String apiPath = "https://i4a402.p.ssafy.io/images/board/";
            List<MultipartFile> files = boardSaveRequestDto.getFiles();
            for(MultipartFile file : files) {
                String changeName = insertedBoardNo + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSSS")) + "_" + file.getOriginalFilename();
                String filePath = rootPath + changeName;
                System.out.println(filePath);
                File dest = new File(filePath);
                file.transferTo(dest);
                pictureRepository.save(Picture.builder().boardNo(insertedBoardNo).boardPic(apiPath + changeName).build());
            }
        }
        return insertedBoardNo;
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public int updateBoard(int boardNo, BoardDto.BoardUpdateRequestDto boardUpdateRequestDto, int userNo) {
        Board board = boardRepository.findById(boardNo).orElseThrow(() -> new IllegalAccessError("[board_no=" + boardNo + "] 해당 게시글이 존재하지 않습니다."));
        if(board.getUserNo() != userNo) {
            return -1;
        }
        board.update(boardUpdateRequestDto.getBoardTitle(), boardUpdateRequestDto.getBoardContent(), boardUpdateRequestDto.getBoardPic(), boardUpdateRequestDto.getUnknownFlag(), boardUpdateRequestDto.getOpenFlag());
        return boardNo;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public int deleteBoard(int boardNo, int userNo) {
        Board board = boardRepository.findById(boardNo).orElseThrow(() -> new IllegalAccessError("[board_no=" + boardNo + "] 해당 게시글이 존재하지 않습니다."));
        if(board.getUserNo() != userNo) {
            return -1;
        }
        board.delete(1);
        boardRepository.save(board);
        return boardNo;
    }

    /**
     * 게시글 복구
     */
    @Transactional
    public int restoreBoard(int board_no, int userNo) {
        Board board = boardRepository.findById(board_no).orElseThrow(() -> new IllegalAccessError("[board_no=" + board_no + "] 해당 게시글이 존재하지 않습니다."));
        if(board.getUserNo() != userNo) {
            return -1;
        }
        board.delete(0);
        boardRepository.save(board);
        return board_no;
    }

    /**
     * 좋아요 추가
     */
    @Transactional
    public int addLikeToBoard(BoardDto.LikeRequestSaveDto likeRequestSaveDto, int userNo) {
        Board board = boardRepository.findById(likeRequestSaveDto.toEntity(userNo).getBoardNo()).orElseThrow(() -> new IllegalAccessError("해당 게시글이 존재하지 않습니다."));
        LikePK likePK = new LikePK();
        likePK.setUserNo(userNo);
        likePK.setBoardNo(likeRequestSaveDto.getBoardNo());
        if (!likeRepository.findById(likePK).isPresent()) {
            board.like(true);
        }
        return likeRepository.save(likeRequestSaveDto.toEntity(userNo)).getBoardNo();
    }

    /**
     * 좋아요 삭제
     */
    @Transactional
    public int deleteLikeToBoard(BoardDto.LikeRequestSaveDto likeRequestSaveDto, int userNo) {
        Board board = boardRepository.findById(likeRequestSaveDto.toEntity(userNo).getBoardNo()).orElseThrow(() -> new IllegalAccessError("해당 게시글이 존재하지 않습니다."));
        board.like(false);
        likeRepository.delete(likeRequestSaveDto.toEntity(userNo));
        return board.getBoardNo();
    }

    /**
     * 해당 게시글 좋아요 누른 유저 조회
     */
    @Transactional(readOnly = true)
    public List<String> findAllLikesByBoardNo(int boardNo) {
        List<BoardDto.LikeResponseDto> likeResponseDtoList = likeRepository.findAll().stream().filter(boardLike -> boardLike.getBoardNo() == boardNo).map(BoardDto.LikeResponseDto::new).collect(Collectors.toList());
        List<String> likeUserList = new ArrayList<String>();
        for (BoardDto.LikeResponseDto likeResponseDto : likeResponseDtoList) {
            likeUserList.add(userRepository.findById(likeResponseDto.getUserNo()).get().getUserNick());
        }
        return likeUserList;
    }

    /**
     * 유저별 좋아요 누른 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public List<BoardDto.BoardResponseDto> findAllLikesByUserNo(int userNo) {
        List<BoardDto.LikeResponseDto> likeResponseDtoList = likeRepository.findAll().stream().filter(boardLike -> boardLike.getUserNo() == userNo).map(BoardDto.LikeResponseDto::new).collect(Collectors.toList());
        List<Integer> likeBoardList = new ArrayList<Integer>();
        for (BoardDto.LikeResponseDto likeResponseDto : likeResponseDtoList) {
            likeBoardList.add(likeResponseDto.getBoardNo());
        }
        List<BoardDto.BoardResponseDto> boardResponseDtoList = boardRepository.findAll()
                                                                            .stream()
                                                                            .filter(board -> board.getBoardIsDeleted() == 0)
                                                                            .filter(board -> likeBoardList.contains(board.getBoardNo()))
                                                                            .sorted(Comparator.comparing(Board::getBoardCreatedAt).reversed())
                                                                            .map(BoardDto.BoardResponseDto::new)
                                                                            .map(boardResponseDto -> {
                                                                                User user = userRepository.findById(boardResponseDto.getUserNo()).get();
                                                                                boardResponseDto.setUserNick(user.getUserNick());
                                                                                boardResponseDto.setUserPic(user.getUserPic());
                                                                                boardResponseDto.setBoardCommentCnt(commentRepository.findAll().stream().filter(comment -> comment.getBoard().getBoardNo() == boardResponseDto.getBoardNo()).collect(Collectors.toList()).size());
                                                                                boardResponseDto.setFiles(pictureRepository.findPicByBoardNo(boardResponseDto.getBoardNo()));
                                                                                return boardResponseDto;
                                                                            })
                                                                            .collect(Collectors.toList());
        return boardResponseDtoList;
    }
}
