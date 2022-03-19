package com.example.assignment

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


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
        composable(route = NOTE_ROUTE){ NoteView()}
    }
    }

@Composable
fun HomeView(){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)){

    }
}
@Composable
fun NoteView(){

    var noteText by remember {
        mutableStateOf("")
    }
    
    val noteVM = viewModel<NoteViewModel>(LocalContext.current as ComponentActivity)
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)
        .padding(10.dp),
        horizontalAlignment =  Alignment.CenterHorizontally,
    ){
            OutlinedTextField(value = noteText,
                onValueChange = {noteText = it},
            label = { Text(text = "to do")})
        OutlinedButton(onClick = {
            noteVM.addNote(Note(noteText))
        }) {
            Text(text = "add a note")
        }
        
        noteVM.notes.value.forEach{
            Divider(thickness = 2.dp)
            Text(text = it.message)
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
        OutlinedButton(onClick = {userVM.logoutUser()}) {
            Text(text = "Log out")
            
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
            Text(text="Log in")
        }
    
    }
}