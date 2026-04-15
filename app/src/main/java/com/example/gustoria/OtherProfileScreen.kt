package com.example.gustoria

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.ShareNavbar
import com.example.gustoria.ui.theme.GustoriaTheme

@Preview(name = "Portrait", showSystemUi = true)
@Composable
fun OtherProfileScreenPortrait() {
    GustoriaTheme(dynamicColor = false) {
        OtherProfileScreen(
            viewModel = viewModel(),
            onBack = {}
        )
    }
}

@Preview(name = "Landscape", showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
fun OtherProfileScreenLandscape() {
    GustoriaTheme(dynamicColor = false) {
        OtherProfileScreen(
            viewModel = viewModel(),
            onBack = {}
        )
    }
}

@Composable
fun ProfileImage(image_url: String?) {
    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.guest_user_profile_pic),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop, // crops to fill the circle
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, Color.LightGray, CircleShape) // width, color, shape
                .clip(CircleShape)
        )
    }
}

@Composable
fun ProfileInfo(fullName: String, nickname: String, description: String){
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Full Name and Nickname
        Text("${fullName} (${nickname})", fontSize = 20.sp)

        // Description
        Text(
            text = description,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ValueBox (
    value: Int,
    text: String,
) {
    Box(
        modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).width(100.dp).height(70.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = value.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(text = text.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
        }
    }
}

@Composable
fun OtherProfileScreen(viewModel: OtherProfileViewModel, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        ShareNavbar(modifier = Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colorScheme.background), title = "Other Profile", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ProfileImage(
                    image_url = viewModel.user.profileImageUri
                )
            }

            item {
                ProfileInfo(
                    fullName = viewModel.user.fullName,
                    nickname = viewModel.user.nickname,
                    description = viewModel.user.description
                )
            }

            item {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = viewModel::toggleFollow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.isFollowing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = if (viewModel.isFollowing) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = if (viewModel.isFollowing) "Unfollow" else "Follow")
                    }
                }
            }

            item {
                FlowRow(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    maxItemsInEachRow = 3,
                ) {
                    ValueBox(viewModel.user.numberOfRecipes, "Recipes")
                    ValueBox(viewModel.user.numberOfFollowers, "Followers")
                    ValueBox(viewModel.user.numberOfLikes, "Likes")
                }
            }
        }
    }
}
