package com.example.mapsapp.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mapsapp.model.Routes
import com.example.mapsapp.model.UserPrefs
import com.example.mapsapp.viewModel.viewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignInScreen(
    navController: NavHostController,
    viewModel: viewModel,
    userPrefs : UserPrefs
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    var show = viewModel.show.collectAsState()

    val storedUserData = userPrefs.getUserData.collectAsState(initial = emptyList())
    if (storedUserData.value.isNotEmpty()
        && storedUserData.value[0] != ""
        && storedUserData.value[1] != "") {
        viewModel.login(viewModel.auth, storedUserData.value[0], storedUserData.value[1], userPrefs)
        if (viewModel.goToNext.value == true) {
            navController.navigate(Routes.Pantalla2.route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = email.value,
            onValueChange = { viewModel.updateEmail(it) },
            placeholder = { Text("E-mail") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") }
        )

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = password.value,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = { Text("Password") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Email") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        Button(
            onClick = {
                    if (password.value.isNotEmpty() && email.value.isNotEmpty()) {
                        viewModel.login(viewModel.auth, viewModel.getEmail(), viewModel.getPass(), userPrefs)
                    } else {
                        viewModel.changeError("All fields must be filled")
                        viewModel.changeShow()
                    }
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = "Sign-in",
                fontSize = 16.sp,
                modifier = Modifier.padding(0.dp, 6.dp)
            )
        }

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp))

        TextButton(onClick = { navController.navigate(Routes.Pantalla1.route) }) {
            Text(text = "Sign-up", fontSize = 16.sp)
        }

        ErrorDialog(show.value, viewModel) { viewModel.changeShow() }
    }
}
