package com.example.culinar.models.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Community
import com.example.culinar.models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsible for managing community-related operations such as:
 * - Fetching all communities and the user's own community
 * - Managing user membership (add/remove/check)
 * - Selecting a community and fetching its posts
 * - Creating and liking/unliking posts in communities
 *
 * Firebase Firestore is used as the backend storage.
 */
class CommunityViewModel : ViewModel() {

	private val db = Firebase.firestore

	// userId exposé en lecture seule (StateFlow)
	private val _userId = MutableStateFlow("")
	val userId: StateFlow<String> get() = _userId

	// Toutes les communautés disponibles dans la base
	var allCommunities: MutableStateFlow<List<Community>> = MutableStateFlow(emptyList())

	// Ma propre communauté (celle dont l'ID est égal à mon userId)
	private val _myCommunity = MutableStateFlow<Community?>(null)
	val myCommunity: StateFlow<Community?> = _myCommunity.asStateFlow()

	// Communauté actuellement sélectionnée par l'utilisateur
	private val _selectedCommunity = MutableStateFlow<Community?>(null)
	var selectedCommunity: MutableStateFlow<Community?> = MutableStateFlow(null)

	// Map des statuts de membre : [communityId] -> isMember (true/false)
	var isMember: MutableStateFlow<MutableMap<String, Boolean>> = MutableStateFlow(mutableMapOf())

	// Liste des posts de la communauté sélectionnée
	var allPosts: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())

	init {
		// Lorsque l'ID utilisateur est défini, on rafraîchit les communautés
		viewModelScope.launch {
			_userId.collect { id ->
				if (id.isNotEmpty()) {
					refreshCommunities()
				}
			}
		}
	}

	/**
	 * Met à jour l'identifiant utilisateur courant
	 * @param id Identifiant utilisateur
	 */
	fun setUserId(id: String) {
		_userId.value = id
	}

	/**
	 * Rafraîchit toutes les informations liées aux communautés
	 * (liste complète, communauté de l'utilisateur, appartenance)
	 */
	fun refreshCommunities() {
		getCommunities()
		getMyCommunity()
		viewModelScope.launch {
			allCommunities.collect { communitiesList ->
				if (communitiesList.isNotEmpty()) {
					checkMemberships(communitiesList)
				}
			}
		}
	}

	/**
	 * Définit la communauté sélectionnée par l'utilisateur
	 * et déclenche le chargement des posts associés.
	 * @param community La communauté sélectionnée
	 */
	fun selectCommunity(community: Community) {
		_selectedCommunity.value = community
		Log.d("CommunityViewModel", "Community selected: ${community.id}. Fetching posts.")
		getPosts(community.id)
	}

	/**
	 * Récupère toutes les communautés (sauf celle correspondant à mon propre userId)
	 * Trie la liste pour faire apparaître en tête celles dont je suis membre
	 */
	fun getCommunities() {
		viewModelScope.launch {
			try {
				val result = db.collection(COMMUNITY_FIREBASE_COLLECTION).get().await()
				val toSort = mutableListOf<Community>()
				val communities = mutableListOf<Community>()

				for (document in result) {
					if (document.id != _userId.value) {
						val community = document.toObject(Community::class.java)
						community.id = document.id

						val memberIdsSnapshot = db.collection(COMMUNITY_FIREBASE_COLLECTION)
							.document(document.id)
							.collection("members")
							.get()
							.await()
						val memberIds = memberIdsSnapshot.documents.map { it.id }
						community.members = memberIds.toMutableList()

						toSort.add(community)
					}
				}

				toSort.forEach { community ->
					if (community.members?.contains(_userId.value) == true)
						communities.add(0, community)
					else
						communities.add(community)
				}

				allCommunities.value = communities
				Log.d("CommunityViewModel", "Communities loaded: ${communities.size}")

			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting communities: $e")
			}
		}
	}

	/**
	 * Récupère ma communauté personnelle (ID = mon userId)
	 */
	fun getMyCommunity() {
		viewModelScope.launch {
			try {
				val result = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(_userId.value)
					.get()
					.await()
				val community = result.toObject(Community::class.java)
				community?.id = _userId.value
				_myCommunity.value = community
				Log.d("CommunityViewModel", "My community loaded")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting my community: $e")
				_myCommunity.value = null
			}
		}
	}

	/**
	 * Crée une nouvelle communauté avec mon ID comme identifiant
	 * et m’ajoute comme membre automatiquement.
	 */
	fun addCommunity(community: Community) {
		viewModelScope.launch {
			try {
				db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(_userId.value)
					.set(community)
					.await()
				addMember(community.id)
				refreshCommunities()
				Log.d("CommunityViewModel", "Community added")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error adding community: $e")
			}
		}
	}

	/**
	 * Met à jour une communauté existante (remplace entièrement le document)
	 * @param community Communauté mise à jour
	 */
	fun updateCommunity(community: Community) {
		viewModelScope.launch {
			try {
				db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(_userId.value)
					.set(community)
					.await()
				Log.d("CommunityViewModel", "Community updated")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error updating community: $e")
			}
		}
	}

	/**
	 * Récupère les identifiants des membres d'une communauté donnée
	 * @param communityId ID de la communauté
	 */
	suspend fun getCommunityMembers(communityId: String): List<String> {
		return try {
			val snapshot = db.collection(COMMUNITY_FIREBASE_COLLECTION)
				.document(communityId)
				.collection("members")
				.get()
				.await()
			snapshot.documents.map { it.id }
		} catch (e: Exception) {
			Log.e("CommunityViewModel", "Error getting members for $communityId: $e")
			emptyList()
		}
	}

	/**
	 * Vérifie, pour chaque communauté, si l'utilisateur en est membre
	 * @param communitiesToScan Liste des communautés à vérifier
	 */
	suspend fun checkMemberships(communitiesToScan: List<Community>) {
		if (communitiesToScan.isEmpty()) {
			isMember.value = mutableMapOf()
			return
		}
		val currentMembership = isMember.value.toMutableMap()
		for (community in communitiesToScan) {
			try {
				val membersSnapshot = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(community.id)
					.collection("members")
					.get()
					.await()
				currentMembership[community.id] = membersSnapshot.documents.any { it.id == _userId.value }
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error checking membership for ${community.id}: $e")
				currentMembership[community.id] = false
			}
		}
		isMember.value = currentMembership
	}

	/**
	 * Ajoute un utilisateur à une communauté.
	 * @param communityId ID de la communauté
	 * @param userId ID de l'utilisateur à ajouter (défaut = moi)
	 */
	suspend fun addMember(communityId: String, userId: String = _userId.value): Boolean {
		return try {
			db.collection(COMMUNITY_FIREBASE_COLLECTION)
				.document(communityId)
				.collection("members")
				.document(userId)
				.set(mapOf("userId" to userId))
				.await()

			val currentCommunities = allCommunities.value.toMutableList()
			currentCommunities.find { it.id == communityId }?.let {
				it.members = (it.members ?: mutableListOf()).toMutableList().apply { add(userId) }
			}
			allCommunities.value = currentCommunities

			checkMemberships(currentCommunities)
			Log.d("CommunityViewModel", "Member added to $communityId")
			true
		} catch (e: Exception) {
			Log.e("CommunityViewModel", "Error adding member to $communityId: $e")
			false
		}
	}

	/**
	 * Supprime un utilisateur d'une communauté.
	 * @param communityId ID de la communauté
	 * @param userId ID de l'utilisateur à retirer
	 */
	suspend fun removeMember(communityId: String, userId: String = _userId.value): Boolean {
		return try {
			db.collection(COMMUNITY_FIREBASE_COLLECTION)
				.document(communityId)
				.collection("members")
				.document(userId)
				.delete()
				.await()

			val currentCommunities = allCommunities.value.toMutableList()
			currentCommunities.find { it.id == communityId }?.let {
				it.members = (it.members ?: mutableListOf()).toMutableList().apply { remove(userId) }
			}
			allCommunities.value = currentCommunities

			checkMemberships(currentCommunities)
			Log.d("CommunityViewModel", "Member removed from $communityId")
			true
		} catch (e: Exception) {
			Log.e("CommunityViewModel", "Error removing member from $communityId: $e")
			false
		}
	}

	/**
	 * Charge tous les posts d'une communauté et les trie par date décroissante
	 * @param communityId ID de la communauté
	 */
	fun getPosts(communityId: String) {
		viewModelScope.launch {
			try {
				val result = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(communityId)
					.collection("posts")
					.get()
					.await()
				val fetchedPosts = result.documents.mapNotNull { doc ->
					val post = doc.toObject(Post::class.java)
					post?.id = doc.id
					post
				}.sortedByDescending { it.date }

				allPosts.value = fetchedPosts
				Log.d("CommunityViewModel", "Posts loaded for $communityId, count: ${fetchedPosts.size}")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting posts for $communityId: $e")
				allPosts.value = emptyList()
			}
		}
	}

	// Vérifie si l'utilisateur a déjà liké un post donné
	fun hasLiked(post: Post): Boolean {
		return post.likes.contains(_userId.value)
	}

	/**
	 * Crée un post dans la communauté courante (et public si nécessaire)
	 * @param post Objet Post à enregistrer
	 * @param communityId ID de la communauté (défaut : ma communauté)
	 */
	fun createPost(post: Post, communityId: String = myCommunity.value?.id ?: "") {
		if (communityId.isEmpty()) {
			Log.e("CommunityViewModel", "Cannot create post: communityId is empty")
			return
		}
		post.authorId = _userId.value
		post.communityId = communityId

		viewModelScope.launch {
			try {
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(communityId)
					.collection("posts")
					.add(post)
					.addOnSuccessListener {
						it.update(mapOf("id" to it.id))
					}
					.await()
				post.id = postRef.id
				getPosts(communityId)
				Log.d("CommunityViewModel", "Post created in $communityId")

				if (!post.isPrivate) {
					try {
						db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
							.document(postRef.id).set(post.toMap())
							.await()
						Log.d("CommunityViewModel", "Post added to public feed")
					} catch (e: Exception) {
						Log.d("CommunityViewModel", "Error adding post to public feed: $e")
					}
				}

			} catch (e: Exception) {
				Log.d("CommunityViewModel", "Error creating post in $communityId: $e")
			}
		}
	}

	/**
	 * Ajoute un like au post donné
	 * @param post Le post à liker
	 * @param userId L'utilisateur qui like
	 */
	fun likePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(_selectedCommunity.value?.id ?: return@launch)
					.collection("posts")
					.document(post.id)

				val updatedLikes = post.likes.toMutableList().apply {
					if (!contains(userId)) add(userId)
				}

				postRef.update("likes", updatedLikes).await()
				updateLocalPostLikes(post.id, updatedLikes)
				Log.d("CommunityViewModel", "Post liked by $userId")

				if (!post.isPrivate) {
					try {
						db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
							.document(post.id)
							.update("likes", updatedLikes)
							.await()
						Log.d("CommunityViewModel", "Post likes updated in public feed")
					} catch (e: Exception) {
						Log.d("CommunityViewModel", "Error updating post likes in public feed: $e")
					}
				}

			} catch (e: Exception) {
				Log.d("CommunityViewModel", "Error liking post: $e")
			}
		}
	}

	/**
	 * Retire un like du post donné
	 * @param post Le post à unliker
	 * @param userId L'utilisateur qui retire son like
	 */
	fun unlikePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(_selectedCommunity.value?.id ?: return@launch)
					.collection("posts")
					.document(post.id)

				val updatedLikes = post.likes.toMutableList().apply {
					remove(userId)
				}

				postRef.update("likes", updatedLikes).await()
				updateLocalPostLikes(post.id, updatedLikes)
				Log.d("CommunityViewModel", "Post unliked by $userId")

				if (!post.isPrivate) {
					try {
						db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
							.document(post.id)
							.update("likes", updatedLikes)
							.await()
						Log.d("CommunityViewModel", "Post likes updated in public feed")
					} catch (e: Exception) {
						Log.d("CommunityViewModel", "Error updating post likes in public feed: $e")
					}
				}

			} catch (e: Exception) {
				Log.d("CommunityViewModel", "Error unliking post: $e")
			}
		}
	}

	/**
	 * Met à jour localement le nombre de likes d’un post donné
	 * @param postId L’identifiant du post
	 * @param updatedLikes Nouvelle liste de likes
	 */
	private fun updateLocalPostLikes(postId: String, updatedLikes: List<String>) {
		val updatedPosts = allPosts.value.map {
			if (it.id == postId) it.copy(likes = updatedLikes.toMutableList()) else it
		}
		allPosts.value = updatedPosts
	}
}

