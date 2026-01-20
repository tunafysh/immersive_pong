import 'package:flutter/material.dart';
import 'package:immersive_pong/colors.dart';
import 'sp.ui.dart';
import 'mp.ui.dart';

class App extends StatefulWidget {
  const App({super.key});

  @override
  State<App> createState() => _AppState();
}

class _AppState extends State<App> {
  int _selectedIndex = 0; // track selected tab

  final List<Widget> _pages = [Singleplayer(), Multiplayer()];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext ctx) {
    return MaterialApp(
      title: "Immersive Pong",
      debugShowCheckedModeBanner: false,
      darkTheme: zincDarkTheme,
      theme: zincLightTheme,
      themeMode: ThemeMode.system,
      home: Scaffold(
        body: _pages[_selectedIndex], // show current page
        bottomNavigationBar: BottomNavigationBar(
          currentIndex: _selectedIndex,
          onTap: _onItemTapped,
          items: const [
            BottomNavigationBarItem(
              icon: Icon(Icons.person),
              label: "Singleplayer",
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.group),
              label: "Multiplayer",
            ),
          ],
        ),
      ),
    );
  }
}
