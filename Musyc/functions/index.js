'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


/*
 * Created By Saad Mahmud
 */

/*
* Sends Notifiations of different type to a specific user
* Based on their Device TokenId
* And increases the notification counter by one
*/
exports.Notificationx= functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(event => {

  const user_id = event.params.user_id;
  const notification_id = event.params.notification_id;
  const root=event.data.ref.root;
  console.log('Here : ', user_id);
  /*
   * Returns on null value
   */

  if(!event.data.val()){

    return console.log('A Notification has been deleted from the database : ', notification_id);

  }

  /*
   * 'fromUser' query retreives the all the info necessary for the notification
   * of the user who sent the notification
   */

  const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().fromid;
    const body_ = fromUserResult.val().body;
    const title_ = fromUserResult.val().title;
    const type_=fromUserResult.val().type;

    console.log('You have new notification from  : ', from_user_id);

    /*
     * If Follow type notification than do something
     * else like type notification
     */
     if(type_==="follow")
     {
       console.log('In if  : ', type_);

       const userQuery = admin.database().ref(`/uidtoinfo/${from_user_id}/username`).once('value');
       const notification_cnt = admin.database().ref(`/uidtoinfo/${user_id}/cnt`).once('value');
       const deviceToken = admin.database().ref(`/uidtoinfo/${user_id}/token`).once('value');

       //Three different query in a promise
       return Promise.all([userQuery, deviceToken,notification_cnt]).then(result => {

         const userName = result[0].val();
         const token_id = result[1].val();
         const notification_cnt=result[2].val();

         var temp=parseInt(cnt, 10);
         temp=temp+1;
         const notification_cnt_final=temp.toString();
         /*
          * We are creating a 'payload' to create a notification to be sent.
          */
            console.log('userName: ', userName);
            console.log('token: ', token_id);
            console.log('notification count: ', notification_cnt_final);

         const payload = {
           notification: {
             title : `${title_}`,
             body: `${body_}`,
             icon: `${type_}`,
             click_action : "com.inc.musyc.musyc_TARGET_NOTIFICATION"
           },
         };

         /*
          * First we are setting new value of notification count.
          * Then using admin.messaging() we are sending the payload notification to the token_id of
          * the device we retreived.
          */

           return root.child(`uidtoinfo/${user_id}/cnt`).set(notification_cnt_final).then(
               response1=>{admin.messaging().sendToDevice(token_id, payload).then(response => {

             console.log('This was the notification Feature: ');

           }).catch(reason=>{
             console.log(reason);
           })
         });
       });
     }else {

       console.log('In if  : ', type_);

       const userQuery = admin.database().ref(`/uidtoinfo/${from_user_id}/username`).once('value');
       const notification_cnt = admin.database().ref(`/uidtoinfo/${user_id}/cnt`).once('value');
       const deviceToken = admin.database().ref(`/uidtoinfo/${user_id}/token`).once('value');

       //Three different query in a promise
       return Promise.all([userQuery, deviceToken,notification_cnt]).then(result => {

         const userName = result[0].val();
         const token_id = result[1].val();
         const notification_cnt=result[2].val();

         var temp=parseInt(cnt, 10);
         temp=temp+1;
         const notification_cnt_final=temp.toString();
         /*
          * We are creating a 'payload' to create a notification to be sent.
          */
            console.log('userName: ', userName);
            console.log('token: ', token_id);
            console.log('notification count: ', notification_cnt_final);

         const payload = {
           notification: {
             title : `${title_}`,
             body: `${body_}`,
             icon: `${type_}`,
             click_action : "com.inc.musyc.musyc_TARGET_NOTIFICATION"
           },
         };

         /*
          * First we are setting new value of notification count.
          * Then using admin.messaging() we are sending the payload notification to the token_id of
          * the device we retreived.
          */

           return root.child(`uidtoinfo/${user_id}/cnt`).set(notification_cnt_final).then(
               response1=>{admin.messaging().sendToDevice(token_id, payload).then(response => {

             console.log('This was the notification Feature: ');

           }).catch(reason=>{
             console.log(reason);
           })
         });
       });
     }
  });
});
