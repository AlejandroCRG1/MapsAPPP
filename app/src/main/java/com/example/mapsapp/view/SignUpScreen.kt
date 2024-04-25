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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mapsapp.model.Routes
import com.example.mapsapp.model.UserPrefs
import com.example.mapsapp.viewModel.viewModel


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignUpScreen(
    navController: NavHostController,
    viewModel: viewModel
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()

    var show = viewModel.show.collectAsState()

    val context = LocalContext.current
    val userPrefs = UserPrefs(context)
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
            value = confirmPassword.value,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            placeholder = { Text("Confirm password") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Email") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Button(
            onClick = {
                if (password.value.isNotEmpty() && email.value.isNotEmpty()) {
                    if (password.value.length > 6) {
                        if (password.value == confirmPassword.value) viewModel.register(viewModel.auth, viewModel.getEmail(), viewModel.getPass(), userPrefs)
                        else {
                            viewModel.changeError("Passwords are not the same")
                            viewModel.changeShow()
                        }
                    }
                    else {
                        viewModel.changeError("Password needs to have at least 6 characters")
                        viewModel.changeShow()
                    }
                }
                else {
                    viewModel.changeError("All fields must be filled")
                    viewModel.changeShow()
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = "Sign-up",
                fontSize = 16.sp,
                modifier = Modifier.padding(0.dp, 6.dp)
            )
        }

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp))

        TextButton(onClick = { navController.navigate(Routes.Pantalla0.route) }) {
            Text(text = "Sign-in", fontSize = 16.sp)
        }
        ErrorDialog(show.value, viewModel) { viewModel.changeShow() }
    }
}

@Composable
fun ErrorDialog(show : Boolean, viewModel: viewModel, onDismiss: () -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Error") },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = viewModel.error)
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(text = "OK")
                }
            }
        )
    }
}
