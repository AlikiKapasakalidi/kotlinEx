package com.example.assignment

import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


const val HOME_ROUTE = "route"
const val NOTE_ROUTE = "note"


@Composable

fun MainView(){
    val userVM = viewModel<UserView>()

    if (userVM.username.value.isEmpty()){
        LoginView(userVM)
    }else{
        MainScaffoldView()
    }
}

@Composable
fun MainScaffoldView(){


    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar()
        },
    bottomBar = {
        BottomBar(navController)
        },
    content = {
        MainContent(navController)
        }
    )
}


@Composable
fun MainContent(navController: NavHostController){
    NavHost(navController = navController, startDestination = HOME_ROUTE ){
        composable(route = HOME_ROUTE){ HomeView()}
        composable(route = NOTE_ROUTE){ NoteView() }
    }
    }

@Composable
fun HomeView(){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray)){

    }
}


@Composable
fun NoteView(){

    var postText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var pst by remember { mutableStateOf("") }

    val fireStore = Firebase.firestore



    val noteVM = viewModel<NoteViewModel>(LocalContext.current as ComponentActivity)


        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(10.dp),
            horizontalAlignment =  Alignment.CenterHorizontally,
        ){

            Text(
                text = "Dear Diary",
                color = Color(0xFFDAC5CD),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp))

            Text(text = "Your personal blog", color = Color(0xFFDAC5CD))

            OutlinedTextField(value = title,
                onValueChange = {title = it},
                label = { Text(text = "title", color = Color.White)},
                modifier = Modifier
                    .background(Color.DarkGray)
                    .height(60.dp)
                    .border(2.dp, Color(0xFFE91E63)))

            Divider(thickness = 10.dp)

            OutlinedTextField(value = postText,
                onValueChange = {postText = it},
                label = { Text(text = "What's new?", color = Color.White)},
                modifier = Modifier
                    .background(Color.DarkGray)
                    .height(100.dp)
                    .border(2.dp, Color(0xFFE91E63)))



            Divider(thickness = 15.dp)


            OutlinedButton(onClick = {
                fireStore
                    .collection("Published")
                    .document(title)
                    .set(Note(postText))


                noteVM.addNote(Note(postText))
                fireStore
                    .collection("Published")
                    .document(title)
                    .get()
                    .addOnSuccessListener {
                        pst = it.get("post").toString()
                       /* for (doc in it) {
                            Log.d(
                                doc,
                                doc!!["post"].toString()
                            )
                        }*/
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }




            }
            ) {
                Text(text = "Publish",
                    color = Color(0xFFE91E63))
            }

            noteVM.notes.value.forEach{
                Divider(thickness = 2.dp)
                Card(modifier = Modifier
                    .border(2.dp, Color(0xFFE91E63))
                    .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(10.dp)
                    ) {

                        Text(text = pst)

                        Icon(painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription ="delete_note",
                            modifier = Modifier
                                .clickable {}
                                .height(30.dp)
                        )
                    }

                }
            }

        }


}




@Composable
fun BottomBar(navController: NavHostController){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE91E63)),
                horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(painter = painterResource(id = R.drawable.ic_home),
                contentDescription ="home",
                modifier = Modifier.clickable{navController.navigate(HOME_ROUTE)}
                )
            Icon(painter = painterResource(id = R.drawable.ic_todo_list),
                contentDescription ="note",
                        modifier = Modifier.clickable{navController.navigate(NOTE_ROUTE)}
            )

        }
}

@Composable
fun TopBar(){
    val userVM = viewModel<UserView>()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE91E63))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = userVM.username.value)
        OutlinedButton(onClick = {userVM.logoutUser()})  {
            Text(text = "Log out",
            color = Color(0xFFE91E63))
            
        }
    }
}
@Composable
fun LoginView(userVM: UserView) {
    var email by remember{
        mutableStateOf("")
    }
        var password by remember {
            mutableStateOf("")

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = email,
            onValueChange ={email= it},
            label = {Text(text ="email")})

        OutlinedTextField(
            value = password,
            onValueChange ={password= it},
        label = {Text(text ="password")},
        visualTransformation = PasswordVisualTransformation())
        
        OutlinedButton(onClick = { userVM.loginUser(email, password)}) {
            Text(text="Log in", color = Color.Black)
        }
    
    }

       /* Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text (
                modifier = Modifier.padding(bottom = 10.dp, top = 10.dp),
                text = "Enter your email")

            OutlinedTextField(
                value = email,
                onValueChange ={email= it},
                label = {Text(text ="email")})


            Text (
                modifier = Modifier.padding(bottom = 10.dp, top = 10.dp),
                text = "Enter your password")

            OutlinedTextField(
                value = password,
                onValueChange ={password= it},
                label = {Text(text ="password")},
                visualTransformation = PasswordVisualTransformation())


            OutlinedButton(
                onClick = { Firebase.auth.createUserWithEmailAndPassword(email,password)},
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = 120.dp,
                        minHeight = 35.dp
                    )
                    .padding(top = 50.dp)

            ) {
                Text(
                    "Create account",
                    style = MaterialTheme.typography.body2,
                    color = Color.Black
                )
            }

        }*/
    }



