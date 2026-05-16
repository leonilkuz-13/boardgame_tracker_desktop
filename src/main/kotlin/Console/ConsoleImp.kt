package console

import GameManager.GameManagerImpl
import Repository.History
import Repository.HistoryImpl
import Repository.Statistics
import Repository.StatisticsImpl
import battleship.BattleWagon
import battleship.Carrier
import battleship.Cruiser
import battleship.Destroyer
import battleship.Ship
import battleship.SpecialShip
import battleship.Submarine
import common.Coordinate
import common.ManagerResult
import common.Move
import common.MoveResult
import common.PlayerStats
import common.ShipType
import common.SpecialShape
import console.Console
import kotlin.math.min

class ConsoleImpl : Console {
	private val stat: Statistics = StatisticsImpl()
	private val history: History = HistoryImpl()
	private val gameManager = GameManagerImpl(stat, history)

	override fun start() {
		println("> Welcome to the Board Game Tracker!")

		while (true) {
			println("> Enter the command:")

			println("> 1 -- Start a new game")
			println("> 2 -- View player statistics")
			println("> 3 -- Get leaderboard statistics")
			println("> 4 -- View game history and get a replay of a ID match")
			println("> 99 -- Quick test match (Auto-setup)")
			println("> 0 -- Exit")

			val number = safeReadCommand()
			when (number) {
				"1" -> {
					val result = startNewGame()
					when (result) {
						is ManagerResult.Success -> {
							println("> Match started successfully!")
							setupFleetPhase()
							gameManager.switchTurn()
							setupFleetPhase()
							gameManager.switchTurn()
							gameManager.startGame()
							runGameLoop()
						}
						is ManagerResult.Failure -> {
							println("> Failed to start match: ${result.message}")
						}
						is ManagerResult.Cancel -> {
							println("> Match creation canceled.")
						}
					}
				}

				"2" -> {
					println("> enter player name to view statistics")
					val name = safeReadCommand()
					val profile = getPlayerProfile(name)
					if (profile != null) {
						println("> Statistics for player $name:")
						println("> Wins: ${profile.gamesWon}")
						println("> Losses: ${profile.gamesLost}")
						println("> Winrate: ${profile.winRate}")
					} else {
						println("> No profile found for the player $name")
					}
				}

				"3" -> {
					val topPlayers = getLeaderBoard()

					if (topPlayers.isEmpty()) {
						println("> The leaderboard is empty. Play some games first!")
					} else {
						println("> There are up to 10 players on the leaderboard.")
						val size = min(10, topPlayers.size)
						for (index in 0 until size) {
							println("> ${index + 1}: ${topPlayers[index].playerName} - Wins: ${topPlayers[index].gamesWon}, Losses: ${topPlayers[index].gamesLost}, Winrate: ${topPlayers[index].winRate}%")
						}
					}
				}

				"4" -> {
					println("> Enter match ID to load replay: ")
					val inputID = safeReadCommand()
					val matchId = inputID.toIntOrNull()

					if (matchId == null) {
						println("> Invalid ID format. Please enter a number")
						continue
					}

					val matchHistory = getMatchHistory(matchId)
					if (matchHistory == null) {
						println("> Match $matchId not found or history is empty.")
					} else {
						for (index in matchHistory.indices) {
							val move = matchHistory[index].first
							val result = matchHistory[index].second

							val moveDescription = when (move) {
								is Move.Install -> {
									val startCoordinate = move.coordinates.firstOrNull()
									val coordinateStr =
										if (startCoordinate != null) "${startCoordinate.x}${startCoordinate.y}" else "??"
									"Install ship at [$coordinateStr]"
								}

								is Move.SingleAttack -> {
									"Single Shot at [${move.coordinate.x}${move.coordinate.y}]"
								}

								is Move.GrandAttack -> {
									"Bomber Attack at [${move.center.x}${move.center.y}]"
								}

								is Move.Radar -> {
									"Radar Scan at [${move.center.x}${move.center.y}]"
								}
							}

							val resultDescription = when (result) {
								is MoveResult.Success.Hit -> "HIT"
								is MoveResult.Success.Sunk -> "SUNK"
								is MoveResult.Success.Miss -> "MISS"
								is MoveResult.Success.Over -> "GAME OVER"
								is MoveResult.GrandResult -> "BOMBER ATTACK RESULT"
								is MoveResult.ScanResult -> "SCAN RESULT"
								is MoveResult.ShipInstall -> "SHIP INSTALLED"
								is MoveResult.Error.GameError -> "ERROR: ${result.reason}"
								is MoveResult.Error.InvalidMove -> "INVALID MOVE: ${result.reason}"
							}

							println("> [Turn ${index + 1}] Action: $moveDescription -> Result: $resultDescription")

							Thread.sleep(400)
						}
					}
				}

				"0" -> {
					println("> Exiting the Board Game Tracker. Goodbye!")
					return
				}

				"99" -> {
					println("> System: Starting test benchmark...")

					gameManager.abortMatch()

					gameManager.loginPlayer("Tester1")
					gameManager.loginPlayer("Tester2")
					gameManager.startMatch()

					autoSetupFleetPhase()
					gameManager.switchTurn()
					autoSetupFleetPhase()
					gameManager.switchTurn()

					gameManager.startGame()
					runGameLoop()
				}

				else -> {
					println("> Unknown command. Please enter a number from 0 to 4.")
				}
			}
		}
	}

	override fun getLeaderBoard(): List<PlayerStats> {
		return gameManager.getLeaderboard()
	}

	override fun getPlayerProfile(name: String): PlayerStats? {
		return gameManager.getPlayerProfile(name)
	}

	override fun getMatchHistory(id: Int): List<Pair<Move, MoveResult>>? {
		return gameManager.getMatchHistory(id)
	}

	private fun safeReadCommand(): String {
		while (true) {
			val input = readLine() ?: ""
			if (input.isBlank()) {
				println("> Incorrect command input. Please re-enter.")
				continue
			}
			return input.trim()
		}
	}

	private fun loginUntilSuccess(promptMessage: String): ManagerResult {
		while (true) {
			println("> $promptMessage or enter cancel")
			val name = safeReadCommand()

			if (name.lowercase() == "cancel") {
				println("> action canceled")
				return ManagerResult.Cancel
			}

			val result = gameManager.loginPlayer(name)

			if (result is ManagerResult.Success) {
				println("> $name successfully joined the match")
				return ManagerResult.Success
			} else if (result is ManagerResult.Failure) {
				println("> Error: ${result.message}. Please, try again")
			}
		}
	}

	private fun startNewGame(): ManagerResult {

		val log1 = loginUntilSuccess("enter the name of the first player")
		if (log1 is ManagerResult.Cancel) {
			return ManagerResult.Cancel
		}

		val log2 = loginUntilSuccess("enter the name of the second player")
		if (log2 is ManagerResult.Cancel) {
			return ManagerResult.Cancel
		}

		return gameManager.startMatch()
	}

	private fun setupFleetPhase() {
		val currentPlayer = gameManager.getCurrentPlayerName()

		for (shipType in ShipType.entries) {
			var placedCount = 0

			while (placedCount < shipType.maxCount) {
				val size = shipType.size

				println("> ${currentPlayer}, post ${shipType.name} (${placedCount + 1}/${shipType.maxCount})")

				println("> enter the start coordinate: ")
				val line1 = safeReadCommand().trim()
				val startCoordinate = Coordinate.parse(line1)
				if (startCoordinate == null) {
					println("> Invalid coordinate. Try again")
					continue
				}

				var direction = "E"
				if (size > 1) {
					println("> enter direction: ")
					println("> N -- North")
					println("> S -- South")
					println("> W -- West")
					println("> E -- East")

					direction = safeReadCommand().trim().uppercase()
					val validDirections = listOf("N", "S", "W", "E")

					if (direction !in validDirections) {
						println("> Invalid direction. Try again")
						continue
					}
				}

				val shipCoordinates = mutableListOf<Coordinate>()
				var outOfBoundsError = false

				if (shipType.name == "SPECIAL") {
					val selectedShape = askUserForSpecialShape()
					val blueprint = getSpecialShipOffsets(selectedShape)

					val startX = startCoordinate.x.code
					val startY = startCoordinate.y

					for ((dx, dy) in blueprint) {
						val (rotateDX, rotateDY) = when (direction) {
							"N" -> Pair(-dx, -dy)
							"S" -> Pair(dx, dy)
							"W" -> Pair(-dy, dx)
							"E" -> Pair(dy, -dx)
							else -> Pair(dx, dy)
						}
						val newStartX = (startX + rotateDX).toChar()
						val newStartY = startY + rotateDY
						shipCoordinates.add(Coordinate(newStartX, newStartY))
					}
				} else {
					for (index in 0 until size) {
						val nextCoordinate = when (direction) {
							"N" -> Coordinate((startCoordinate.x.code - index).toChar(), startCoordinate.y)
							"S" -> Coordinate((startCoordinate.x.code + index).toChar(), startCoordinate.y)
							"W" -> Coordinate(startCoordinate.x, startCoordinate.y - index)
							"E" -> Coordinate(startCoordinate.x, startCoordinate.y + index)
							else -> startCoordinate
						}

						if (!nextCoordinate.isValid()) {
							outOfBoundsError = true
							break
						}

						shipCoordinates.add(nextCoordinate)
					}
				}

				if (outOfBoundsError) {
					println("> Error: the ship goes out of bounds. Try again")
					continue
				}

				val ship: Ship = when (shipType) {
					ShipType.BATTLE_WAGON -> BattleWagon(shipCoordinates)
					ShipType.CARRIER -> Carrier(shipCoordinates)
					ShipType.CRUISER -> Cruiser(shipCoordinates)
					ShipType.DESTROYER -> Destroyer(shipCoordinates)
					ShipType.SUBMARINE -> Submarine(shipCoordinates)
					ShipType.SPECIAL -> SpecialShip(shipCoordinates)
				}

				val move = Move.Install(ship, shipCoordinates)
				val result = gameManager.handleMove(move)

				when (result) {
					is MoveResult.ShipInstall -> {
						println("> ${shipType.name} installed successfully")
						placedCount++
					}
					is MoveResult.Error.GameError -> {
						println("> installation error: ${result.reason}")
					}
					is MoveResult.Error.InvalidMove -> {
						println("> Error: ${result.reason}")
					}
					else -> {
						println("> Unknown error")
					}
				}
			}
		}
		println("> all ships installed successfully")
	}

	fun drawSpecialShipTypes(shape: SpecialShape): String {
		return when (shape) {
			SpecialShape.BOTTOM_RIGHT -> """
			[X]
			[X]
			[X][X]
		""".trimIndent()

			SpecialShape.BOTTOM_LEFT -> """
			   [X]
			   [X]
			[X][X]
		""".trimIndent()

			SpecialShape.MIDDLE_RIGHT -> """
			[X]
			[X][X]
			[X]
		""".trimIndent()

			SpecialShape.MIDDLE_LEFT -> """
			   [X]
			[X][X]
			   [X]
		""".trimIndent()

			SpecialShape.TOP_RIGHT -> """
			[X][X]
			[X]
			[X]
		""".trimIndent()

			SpecialShape.TOP_LEFT -> """
			[X][X]
			   [X]
			   [X]
		""".trimIndent()
		}
	}

	private fun askUserForSpecialShape(): SpecialShape {
		val shapes = SpecialShape.entries.toTypedArray()

		println("\n---LIST OF AVAILABLE SPECIAL SHIPS---\n")
		shapes.forEachIndexed { index, shape ->
			println("\n[№${index + 1}] Type: ${shape}")
			println(drawSpecialShipTypes(shape))
			println("-----------------------------")
		}

		while (true) {
			println("> Enter the number of the selected drawing:")
			val choiceString = safeReadCommand()
			val choiceNumber = choiceString.toIntOrNull()

			if (choiceNumber == null || choiceNumber !in 1..shapes.size) {
				println("> Error: enter a number from 1 to ${shapes.size}")
				continue
			}

			println("> The number ship type is selected: ${choiceNumber}")
			val picked = shapes[choiceNumber - 1]
			return picked
		}
	}

	private fun getSpecialShipOffsets(shape: SpecialShape): List<Pair<Int, Int>> {
		return when (shape) {
			SpecialShape.TOP_LEFT -> listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 2))
			SpecialShape.TOP_RIGHT -> listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 2))
			SpecialShape.MIDDLE_LEFT -> listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 1))
			SpecialShape.MIDDLE_RIGHT -> listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 1))
			SpecialShape.BOTTOM_LEFT -> listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 0))
			SpecialShape.BOTTOM_RIGHT -> listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 0))
		}
	}

	private fun runGameLoop() {
		var isGameActive = true

		while (isGameActive) {
			val currentPlayer = gameManager.getCurrentPlayerName()

			println("> enter a number for the action for the $currentPlayer: ")
			println("> 1 -- Single Shot")
			println("> 2 -- Bomber Attacks")
			println("> 3 -- Scanning territory")
			println("> 4 -- Exit")

			val line = safeReadCommand().trim().lowercase()

			when (line) {
				"1" -> {
					println("> enter one coordinate for a single shot")
					val lineSingleShot = safeReadCommand().trim()

					val coordinate = Coordinate.parse(lineSingleShot)
					if (coordinate == null) {
						println("> invalid coordinate format or out of bounds. Try again.")
						continue
					}

					val move = Move.SingleAttack(coordinate)
					val result = gameManager.handleMove(move)

					when (result) {
						is MoveResult.Success.Hit -> println("> HIT. target locked")
						is MoveResult.Success.Sunk -> println("> SUNK. Enemy ship destroyed")
						is MoveResult.Success.Miss -> println("> MISS. No target at the coordinate")
						is MoveResult.Success.Over -> {
							println("> GAME OVER. The winner is $currentPlayer")
							isGameActive = false
						}
						is MoveResult.Error.GameError -> println("> Error: ${result.reason}")
						is MoveResult.Error.InvalidMove -> println("> Error: ${result.reason}")
						else -> println("> UNKNOWN. How is that ?")
					}
				}

				"2" -> {
					println("> enter the center coordinate for bomber attack")
					val lineBomber = safeReadCommand().trim()

					val coordinate = Coordinate.parse(lineBomber)
					if (coordinate == null) {
						println("> invalid coordinate format or out of bounds. Try again.")
						continue
					}

					val move = Move.GrandAttack(coordinate)
					val result = gameManager.handleMove(move)

					when (result) {
						is MoveResult.GrandResult -> {
							println("> BOMBER ATTACK RESULTS:")
							for (res in result.results) {
								when (res) {
									is MoveResult.Success.Hit -> println("> HIT: [${res.coordinate.x}${res.coordinate.y}]")
									is MoveResult.Success.Sunk -> println("> SUNK: [${res.coordinate.x}${res.coordinate.y}]")
									is MoveResult.Success.Miss -> println("> MISS: [${res.coordinate.x}${res.coordinate.y}]")
									is MoveResult.Success.Over -> {
										println("> FATAL HIT: [${res.coordinate.x}${res.coordinate.y}]")
										println("> GAME OVER. The winner is $currentPlayer")
										isGameActive = false
									}
								}
							}
						}
						is MoveResult.Error.GameError -> println("> Error: ${result.reason}")
						is MoveResult.Error.InvalidMove -> println("> Error: ${result.reason}")
						else -> println("> UNKNOWN result for Bomber Attack.")
					}
				}

				"3" -> {
					println("> enter the center coordinate for scanning territory")
					val lineScan = safeReadCommand().trim()

					val coordinate = Coordinate.parse(lineScan)
					if (coordinate == null) {
						println("> invalid coordinate format or out of bounds. Try again.")
						continue
					}

					val move = Move.Radar(coordinate)
					val result = gameManager.handleMove(move)
					when (result) {
						is MoveResult.ScanResult -> {
							println("> SCAN COMPLETED. Radar data:")
							if (result.info.isEmpty()) {
								println("> Sector is completely clear.")
							} else {
								for ((coord, status) in result.info) {
									println("> $status: [${coord.x}${coord.y}]")
								}
							}
						}
						is MoveResult.Error.GameError -> println("> Error: ${result.reason}")
						is MoveResult.Error.InvalidMove -> println("> Error: ${result.reason}")
						else -> println("> UNKNOWN result for Scan.")
					}
				}

				"4" -> {
					println("> force game closure")
					gameManager.abortMatch()
					isGameActive = false
					continue
				}

				else -> {
					println("> Unknown command. Please enter a number from 1 to 4.")
				}
			}
		}
	}

	private fun autoSetupFleetPhase() {
		val currentPlayer = gameManager.getCurrentPlayerName()
		println("> System: Automatic fleet setup for $currentPlayer...")

		val testMoves = listOf(
			Move.Install(Carrier(listOf(Coordinate('A', 1), Coordinate('B', 1), Coordinate('C', 1), Coordinate('D', 1), Coordinate('E', 1))), listOf(Coordinate('A', 1), Coordinate('B', 1), Coordinate('C', 1), Coordinate('D', 1), Coordinate('E', 1))),

			Move.Install(BattleWagon(listOf(Coordinate('A', 3), Coordinate('B', 3), Coordinate('C', 3), Coordinate('D', 3))), listOf(Coordinate('A', 3), Coordinate('B', 3), Coordinate('C', 3), Coordinate('D', 3))),

			Move.Install(Cruiser(listOf(Coordinate('A', 5), Coordinate('B', 5), Coordinate('C', 5))), listOf(Coordinate('A', 5), Coordinate('B', 5), Coordinate('C', 5))),
			Move.Install(Cruiser(listOf(Coordinate('E', 5), Coordinate('F', 5), Coordinate('G', 5))), listOf(Coordinate('E', 5), Coordinate('F', 5), Coordinate('G', 5))),
			Move.Install(Cruiser(listOf(Coordinate('I', 5), Coordinate('J', 5), Coordinate('K', 5))), listOf(Coordinate('I', 5), Coordinate('J', 5), Coordinate('K', 5))),

			Move.Install(Destroyer(listOf(Coordinate('A', 7), Coordinate('B', 7))), listOf(Coordinate('A', 7), Coordinate('B', 7))),
			Move.Install(Destroyer(listOf(Coordinate('D', 7), Coordinate('E', 7))), listOf(Coordinate('D', 7), Coordinate('E', 7))),
			Move.Install(Destroyer(listOf(Coordinate('G', 7), Coordinate('H', 7))), listOf(Coordinate('G', 7), Coordinate('H', 7))),
			Move.Install(Destroyer(listOf(Coordinate('J', 7), Coordinate('K', 7))), listOf(Coordinate('J', 7), Coordinate('K', 7))),

			Move.Install(Submarine(listOf(Coordinate('A', 9))), listOf(Coordinate('A', 9))),
			Move.Install(Submarine(listOf(Coordinate('C', 9))), listOf(Coordinate('C', 9))),
			Move.Install(Submarine(listOf(Coordinate('E', 9))), listOf(Coordinate('E', 9))),
			Move.Install(Submarine(listOf(Coordinate('G', 9))), listOf(Coordinate('G', 9))),
			Move.Install(Submarine(listOf(Coordinate('I', 9))), listOf(Coordinate('I', 9))),

			Move.Install(SpecialShip(listOf(Coordinate('M', 1), Coordinate('M', 2), Coordinate('M', 3), Coordinate('N', 3))), listOf(Coordinate('M', 1), Coordinate('M', 2), Coordinate('M', 3), Coordinate('N', 3)))
		)

		for (move in testMoves) {
			val result = gameManager.handleMove(move)
			if (result is MoveResult.Error) {
				println("> Error auto-setup: ${result.reason}")
			}
		}
		println("> System: Fleet of $currentPlayer is ready for battle!")
	}
}