# RuneScape Discord Bot

![License](https://img.shields.io/github/license/Jrod7938/RSDB) ![Version](https://img.shields.io/github/v/release/Jrod7938/RSDB)

A Kotlin-based Discord bot that integrates with RuneScape APIs to provide real-time game information, including player
highscores, item prices, and wiki searches. This bot also allows users to link their RuneScape profiles to their Discord
accounts.

## Features

- **Highscore Command**: Fetch RuneScape highscores for any player.
- **Grand Exchange Command**: Search for items in the Grand Exchange.
- **Flip Command**: Find the best items to flip in the Grand Exchange.
- **Wiki Command**: Search the RuneScape Wiki.
- **Profile Linking**: Link your RuneScape profile to your Discord account.

## Getting Started

### Prerequisites

- **Kotlin**: Ensure you have Kotlin installed on your development machine.
- **Discord Bot Token**: Create a bot on the [Discord Developer Portal](https://discord.com/developers/applications) and
  obtain a token.
- **Gradle**: This project uses Gradle for dependency management.

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Jrod7938/RSDB.git
   cd RSDB
   ```

2. **Configure the Bot**
   You have three options to provide the Discord bot token:
    1. **Passing as an Argument**: Run the bot by passing the token as an argument to the main file.
    2. **Saving as a File**: Save the token as `token.txt` within the `src/main/kotlin` folder.
    3. **Environment Variable**: Save the token as an environment variable named `DISCORD_BOT_TOKEN`.

3. **Build the Project**
   ```bash
   ./gradlew build
   ```

4. **Run the Bot**
   ```bash
   ./gradlew run
   ```

### Commands

| Command      | Description                                          |
|--------------|------------------------------------------------------|
| `/highscore` | Fetch the RuneScape highscore for a player.          |
| `/ge`        | Search for an item in the Grand Exchange.            |
| `/flip`      | Find the best items to flip in the Grand Exchange.   |
| `/wiki`      | Search the RuneScape Wiki for an object or topic.    |
| `/me`        | Link your RuneScape profile to your Discord account. |

### Usage

To use the bot, simply invite it to your Discord server and use the commands listed above. For example:

```bash
/highscore player:Zezima
/ge item:Dragon claws
/me username:Zezima
```

## Logging

The bot uses a centralized logger to track all command executions and user interactions.

## Contributing

We welcome contributions to improve this bot! Here's how you can help:

1. **Fork the Repository**
2. **Create a New Branch** (`git checkout -b feature/your-feature-name`)
3. **Commit Your Changes** (`git commit -m 'Add some feature'`)
4. **Push to the Branch** (`git push origin feature/your-feature-name`)
5. **Open a Pull Request**

Please ensure your code adheres to the existing coding standards and includes relevant documentation.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **Kord Library**: Used for Discord API interactions.
- **RuneScape Wiki API**: Provided the game data integrations.
- **JetBrains**: For the Kotlin language and development tools.

## Contact

For any issues or feature requests, please open an issue on [GitHub](https://github.com/Jrod7938/RSDB).
