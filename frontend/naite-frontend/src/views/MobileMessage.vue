<template>
    <div id='mobilemessagestatesecond'>        
        <div v-if='!messageDetailBoolean'>

            <div class='mobilemessage2ndTitle'  >
                <h4>채팅</h4>            
            </div>
            <div class='mobilemessageList' v-for='(message,idx) in myChatList' :key="idx">
                
                <div class="mobilemessageListBox" @click='messageDetail(message.roomNo,message.otherNick,message.otherPic,message.otherUserNo)'>                    
                    <img :src='message.otherPic' alt="">
                    <div>
                        <span>{{message.otherNick}}</span><br>
                        <div class='mobilemessageListList'>

                            <div>{{message.lastMessage ? message.lastMessage: `${message.otherNick}님과의 채팅방이 개설되었습니다!` }}</div>
                            <div>{{message.lastMessageTime}}</div>
                        </div>
                    </div>
                </div>


            </div>
        </div>
        <MobileMessageDetail :otherNick = 'otherNick' :roomNo = 'roomNo' :otherPic='otherPic' :otherNo='otherNo' @backtosecondstate='backtosecondstate' v-else/>
    </div>
</template>
<script>
import axios from 'axios'
import MobileMessageDetail from '@/components/MobileMessageDetail'

const SERVER_URL = process.env.VUE_APP_SERVER_URL


export default {
    name:'MobileMessage',
    data:function(){
        return {
            myChatList:[],
            messageDetailBoolean:false,
            roomNo:0,
            otherNick:'',
            otherNo:'',
            otherPic:'',
        }
    },
    components:{
        MobileMessageDetail
    },
    methods:{
        backtosecondstate:function(){
            this.messageDetailBoolean = !this.messageDetailBoolean
        },
        renderChatList:function(){
            axios.get(`${SERVER_URL}/chat/room`,this.setToken())
                .then(res => {
                    this.myChatList = res.data                    
                })
                .catch(err => {
                    console.log(err)
                })
        },
        messageDetail:function(roomNo,otherNick,pic,num){
            this.messageDetailBoolean = !this.messageDetailBoolean
            this.roomNo = roomNo
            this.otherNick = otherNick
            this.otherPic = pic
            this.otherNo = num
            

        },
        setToken:function(){
            const token = localStorage.getItem('jwt')
            const config = {
                headers: {
                'auth-token':`${token}`
                }
            }        
            return config 
        }

    },
    created(){
        this.renderChatList()
        const checkRoom = this.$route.params.roomNo
        if (checkRoom > 0) {
            this.roomNo = checkRoom
            this.otherNick = this.$route.params.otherNick
            this.messageDetailBoolean = true
        }
        if (this.$route.params.otherNo) {
            this.otherNo = this.$route.params.otherNo
        }
        if (this.$route.params.otherPic){
            this.otherPic = this.$route.params.otherPic
        }
    }
}
</script>
<style>
#mobilemessagestatesecond{
    display:none;
}
@media screen and (max-width:501px) {
    #mobilemessagestatesecond {
        display:block;
        position:relative;
        width:100%;
        height:86%;
        overflow: auto;
        overflow-x: hidden;
        font-family: font1;
        padding:0 0 0px 0;
        border: 1px solid #3F9F47;
        border-radius: 0px;        
        top:7%;
        
    }
    .mobilemessage2ndTitle {
        padding: 5px 10px;
        display:flex;
        justify-content: space-between;
        border-bottom: 3px solid #ffffff;
        background-color: #c57f3d;
        color:white;
    }

    .mobilemessage2ndTitle > i {
        margin-top: 2%;
    }

    .mobilemessageListBox {
        position:relative;
        width: 100%;
        background-color: #e0a063e5;
        transition:0.3s;
        border-bottom: 1px solid rgb(255, 255, 255);
        display:flex;
        padding:2%;
        color:black;
    }
    .mobilemessageListBox > img {
        margin-right: 2%;
        width: 62px;
        height:62px;
        border-radius: 20%;
        border: 1px solid rgb(156, 156, 156);
    }

    .mobilemessageListBox:hover{
        box-shadow: 5px 5px 5px;
    }

    .mobilemessageListBox > div {
        text-align: left;
        font-size: 15px;
    }

    .mobilemessageListList{
        height:60%;
        width: 100%;

        
        
    }

    .mobilemessageListList :nth-child(1) {    
        width: fit-content;
        max-width: 300px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;    
        margin-top: auto;
        font-size: 12px;
        
        
    }

    .mobilemessageListList :nth-child(2){
        font-size: 10px;
        margin-top: 3px;        
        white-space: nowrap;        
    }
    
}
</style>