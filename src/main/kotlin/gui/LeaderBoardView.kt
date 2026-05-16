//package gui
//
//import androidx.compose.desktop.ui.tooling.preview.Preview
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Window
//import androidx.compose.ui.window.application
//import java.awt.Color
//import javax.swing.GroupLayout
//
//
//// еще есть контейнер Box, который накладывает элементы друг на друга
//@Composable
//fun LeaderBoardView() {
//    // этот column помогает расположить все элементы друг под другом и horizontalAligment помогает расположить все по центру
//    Scaffold(content = { innerPadding: PaddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                // .background(Color.YELLOW) это фон кнопки делает
//                .fillMaxSize() // расположение по всему
//                // обрисовка границ контейнера
//                //.width(200.dp)
//                //.height(200.dp)
//            ,
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center // и куча всяких вариантов поиграться
//        ) {
//            Heading() // это типо контент
//            Spacer(modifier = Modifier.height(30.dp)) // между основными элементы добавляет отступ
//            MainNuvButtons()
//        }
//    })
//}
//
//
//@Composable
//fun Heading() {
//    Column {
//        Text(
//            text = "LeaderBoard",
//            modifier = Modifier.align(Alignment.CenterHorizontally), // выравнивание относительно своего контейнера
//            fontSize = 28.sp
//        )
//        Text(
//            text = "Welcome to LeaderBoard",
//            fontSize = 16.sp
//        )
//    }
//}
//
//@Preview
//@Composable
//fun LeaderBoardPreview() {
//    LeaderBoardView()
//}
//
//// тут я попробую создать кнопки, для LeaderBoard не нужно
//@Composable
//fun MainNuvButtons() {
//    Row {
//        Button(
//            onClick = {},
//            modifier = Modifier
//                .weight(1f) // это типо кнопка занимает равную долю
//                .padding(horizontal = 7.dp) // горизонтальный отступ в 7 пикселей
//        ) {
//            Text("LeaderBoard")
//        }
//        Button(
//            onClick = {},
//            modifier = Modifier
//                .weight(1f) // это типо кнопка занимает равную долю
//                .padding(horizontal = 7.dp) // горизонтальный отступ в 7 пикселей
//        ) {
//            Text("LeaderBoard")
//        }
//        Button(
//            onClick = {},
//            modifier = Modifier
//                .weight(1f) // это типо кнопка занимает равную долю
//                .padding(horizontal = 7.dp) // горизонтальный отступ в 7 пикселей
//        ) {
//            Text("LeaderBoard")
//        }
//    }
//}
//
//@Composable
//@Preview()
//fun MainNuvButtonsPreview() {
//    MainNuvButtons()
//}