package com.example.lourinhamuseum.data.domain

import com.example.fetchfromserver.domain.Content
import com.example.fetchfromserver.domain.MuseumRoom
import com.example.lourinhamuseum.data.database.entities.DatabaseMuseum
import com.example.lourinhamuseum.screens.allCards.DataItem


data class Museum(
    val id: Int,
    val title: String,
    val contents: List<Content>,
    val rooms: List<MuseumRoom>,
    var score: Int,
    var isScoreUpdated: Boolean = false
) {

    private val allPoints: MutableMap<Int, Point> = mutableMapOf()
    private val _filesToDownload: MutableList<String> = mutableListOf()
    private val _recyclerViewPoints: MutableList<DataItem> = mutableListOf()
    private val _headersIndexes: MutableList<Int> = mutableListOf()
    var numPointsToFind = 0

    val filesToDownload: List<String>
        get() = _filesToDownload
    val recyclerViewPoints: List<DataItem>
        get() = _recyclerViewPoints
    val headersIndexes: List<Int>
        get() = _headersIndexes

    init {
        computeMuseumNecessaryInformation()
    }

    /**
     * Constructs a list of all files that need to be downloaded for the museum
     * presentation (images and audio files)
     * Computes a collection of all points in the museum, in a map
     * Computes a list to be displayed by th recycler view
     */
    private fun computeMuseumNecessaryInformation() {
        for (room in rooms) {
            getPointToRecyclerView(room)
            for (point in room.points) {
                addFilesToDownload(point)
                addRoomPoints(point)
            }
        }
    }

    /**
     * Adds all files need to download associated with a point, to the [filesToDownload]
     * @param point with the files
     */
    private fun addFilesToDownload(point: Point) {
        addFile(point.imageNotFound)
        addFile(point.outlineImage)
        addFile(point.imageFound)
        addFile(point.media)
        addFile(point.video)
        _filesToDownload.addAll(point.slideshow.filesToDownload())
    }

    /**
     * If the file has a valid name add the file to the list [filesToDownload]
     */
    private fun addFile(file: String) {
        if (file != "") {
            _filesToDownload.add(file)
        }
    }

    /**
     * Adds all points of the given room to [allPoints]
     * @param room room with the point to add to the museum points collection
     */
    private fun addRoomPoints(point: Point) {
            allPoints[point.id] = point
            if(!point.isFound){
                numPointsToFind++
            }
    }

    /**
     * Constructs a list of items to be used by the [RecyclerView], with all the
     * [Point] in the [Museum]. To allow the separation of [Point] by [MuseumRoom] the
     * data is wrapped in a [DataItem], with all [Point] in [DataItem.PointItem] and a
     * [DataItem.Header] separations [Point] in different [MuseumRoom].
     * Also stores the indexes of the [DataItem.Header], so it can be defined a
     * different span
     * @param room room to start to add the points and the header
     */
    private fun getPointToRecyclerView(room: MuseumRoom) {
        //ATTENTION se se quiser a divisão por salas
//        _headersIndexes.add(_recyclerViewPoints.size)
//        _recyclerViewPoints.add(DataItem.Header(room))
        _recyclerViewPoints.addAll(room.points.map { DataItem.PointItem(it) })
    }

    /**
     * Retrieves a specific point from the museum point collection
     * @param id o the point to get, if exists
     * @return the desired point
     */
    fun getPoint(id: Int): Point? {
        return allPoints[id]
    }

    fun asDatabaseMuseum(): DatabaseMuseum {
        return DatabaseMuseum(id, title, score)
    }
}