package com.app.academy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.academy.data.source.AcademyDataSource
import com.app.academy.data.source.NetworkBoundResource
import com.app.academy.data.source.local.LocalDataSource
import com.app.academy.data.source.local.entity.ContentEntity
import com.app.academy.data.source.local.entity.CourseEntity
import com.app.academy.data.source.local.entity.CourseWithModule
import com.app.academy.data.source.local.entity.ModuleEntity
import com.app.academy.data.source.remote.ApiResponse
import com.app.academy.data.source.remote.RemoteDataSource
import com.app.academy.data.source.remote.response.ContentResponse
import com.app.academy.data.source.remote.response.CourseResponse
import com.app.academy.data.source.remote.response.ModuleResponse
import com.app.academy.utils.AppExecutors
import com.app.academy.vo.Resource

class FakeAcademyRepository constructor(
	private val remoteDataSource: RemoteDataSource,
	private val localDataSource: LocalDataSource,
	private val appExecutors: AppExecutors
) :
	AcademyDataSource {

	override fun getAllCourses(): LiveData<Resource<List<CourseEntity>>> {
		return object :
			NetworkBoundResource<List<CourseEntity>, List<CourseResponse>>(appExecutors) {
			public override fun loadFromDB(): LiveData<List<CourseEntity>> =
				localDataSource.getAllCourses()

			override fun shouldFetch(data: List<CourseEntity>?): Boolean =
				data == null || data.isEmpty()

			public override fun createCall(): LiveData<ApiResponse<List<CourseResponse>>> =
				remoteDataSource.getAllCourses()

			public override fun saveCallResult(data: List<CourseResponse>) {
				val courseList = ArrayList<CourseEntity>()
				for (response in data) {
					val course = CourseEntity(
						response.id,
						response.title,
						response.description,
						response.date,
						false,
						response.imagePath
					)
					courseList.add(course)
				}

				localDataSource.insertCourses(courseList)
			}
		}.asLiveData()
	}

	override fun getBookmarkedCourses(): LiveData<List<CourseEntity>> =
		localDataSource.getBookmarkedCourses()

	override fun getCourseWithModules(courseId: String): LiveData<Resource<CourseWithModule>> {
		return object : NetworkBoundResource<CourseWithModule, List<ModuleResponse>>(appExecutors) {
			override fun loadFromDB(): LiveData<CourseWithModule> =
				localDataSource.getCourseWithModules(courseId)

			override fun shouldFetch(data: CourseWithModule?): Boolean =
				data?.mModules == null || data.mModules.isEmpty()

			override fun createCall(): LiveData<ApiResponse<List<ModuleResponse>>> =
				remoteDataSource.getModules(courseId)

			override fun saveCallResult(data: List<ModuleResponse>) {
				val moduleList = ArrayList<ModuleEntity>()
				for (response in data) {
					val course = ModuleEntity(
						response.moduleId,
						response.courseId,
						response.title,
						response.position,
						false
					)
					moduleList.add(course)
				}
				localDataSource.insertModules(moduleList)
			}
		}.asLiveData()
	}

	override fun getAllModulesByCourse(courseId: String): LiveData<Resource<List<ModuleEntity>>> {
		return object :
			NetworkBoundResource<List<ModuleEntity>, List<ModuleResponse>>(appExecutors) {
			override fun loadFromDB(): LiveData<List<ModuleEntity>> =
				localDataSource.getAllModulesByCourse(courseId)


			override fun shouldFetch(data: List<ModuleEntity>?): Boolean =
				data == null || data.isEmpty()


			override fun createCall(): LiveData<ApiResponse<List<ModuleResponse>>> =
				remoteDataSource.getModules(courseId)


			override fun saveCallResult(data: List<ModuleResponse>) {
				val moduleList = ArrayList<ModuleEntity>()
				for (response in data) {
					val course = ModuleEntity(
						response.moduleId,
						response.courseId,
						response.title,
						response.position,
						false
					)
					moduleList.add(course)
				}
				localDataSource.insertModules(moduleList)
			}
		}.asLiveData()
	}

	override fun getContent(moduleId: String): LiveData<Resource<ModuleEntity>> {
		return object : NetworkBoundResource<ModuleEntity, ContentResponse>(appExecutors) {
			override fun loadFromDB(): LiveData<ModuleEntity> =
				localDataSource.getModuleWithContent(moduleId)

			override fun shouldFetch(data: ModuleEntity?): Boolean =
				data?.contentEntity == null

			override fun createCall(): LiveData<ApiResponse<ContentResponse>> =
				remoteDataSource.getContent(moduleId)

			override fun saveCallResult(data: ContentResponse) =
				localDataSource.updateContent(data.content, moduleId)

		}.asLiveData()
	}


	override fun setCourseBookmark(course: CourseEntity, state: Boolean) {
		appExecutors.diskIO().execute { localDataSource.setCourseBookmark(course, state) }
	}

	override fun setReadModule(module: ModuleEntity) {
		appExecutors.diskIO().execute { localDataSource.setReadModule(module) }
	}
}