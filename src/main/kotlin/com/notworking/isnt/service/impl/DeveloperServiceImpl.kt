package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Follow
import com.notworking.isnt.repository.*
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeveloperServiceImpl(
    val developerRepository: DeveloperRepository,
    val issueRepository: IssueRepository,
    val solutionRepository: SolutionRepository,
    val commentRepository: CommentRepository,
    val recommendRepository: RecommendRepository,
    val followRepository: FollowRepository,
    val passwordEncoder: PasswordEncoder
) :
    DeveloperService {

    @Transactional
    override fun saveDeveloper(developer: Developer) {
        developer.let {
            // 아이디 중복 체크
            if (developerRepository.existsByUserId(developer.userId)) {
                throw BusinessException(Error.DEVELOPER_USERID_DUPLICATION)
            }
            // 이름 중복 체크
            if (developerRepository.existsByName(developer.name)) {
                throw BusinessException(Error.DEVELOPER_NAME_DUPLICATION)
            }
            it.pwd = passwordEncoder.encode(it.pwd)
            developerRepository.save(it)
        }
    }

    override fun findAllDeveloper(): List<Developer> {
        return developerRepository.findAll().toList()
    }

    override fun findDeveloperByName(name: String): Developer? {
        return developerRepository.findByName(name)
    }

    override fun findDeveloperByUserId(userId: String): Developer? {
        return developerRepository.findByUserId(userId)
    }

    @Transactional
    override fun updateDeveloper(newDeveloper: Developer): Developer? {
        var developer: Developer? = newDeveloper.userId?.let { developerRepository.findByUserId(it) }
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        var dupDeveloper = developerRepository.findByName(newDeveloper.name)

        // 이름 중복 체크
        if (!(dupDeveloper == null || dupDeveloper.id == developer.id)) {
            throw BusinessException(Error.DEVELOPER_NAME_DUPLICATION)
        }
        // null일 경우 예외처리
        developer.update(newDeveloper)

        return developer
    }

    @Transactional
    override fun updatePasswordDeveloper(userId: String, password: String) {
        var developer: Developer? = developerRepository.findByUserId(userId)

        // null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        developer.pwd = passwordEncoder.encode(password)
    }

    @Transactional
    override fun deleteDeveloper(userId: String) {
        var developer: Developer? = developerRepository.findByUserId(userId)
        var withdrawalDeveloper: Developer? = developerRepository.findByUserId("withdrawalDeveloper")
        // 삭제할 사용자가 null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        // 탈퇴회원용 사용자가 null일 경우 예외처리
        withdrawalDeveloper ?: throw BusinessException(Error.WITHDRAWAL_DEVELOPER_NOT_FOUND)

        // 사용자가 작성한 이슈, 솔루션, 코멘트, 추천들을 탈퇴회원계정으로 변경
        issueRepository.findAllByDeveloper(developer).forEach {
            it.developer = withdrawalDeveloper
        }

        solutionRepository.findAllByDeveloper(developer).forEach {
            it.developer = withdrawalDeveloper
        }

        commentRepository.findAllByDeveloper(developer).forEach {
            it.developer = withdrawalDeveloper
        }

        recommendRepository.findAllByDeveloper(developer).forEach {
            it.developer = withdrawalDeveloper
        }

        developerRepository.delete(developer)
    }

    override fun existsDeveloperByUserId(userId: String): Boolean {
        return developerRepository.existsByUserId(userId)
    }

    override fun existsDeveloperByName(name: String): Boolean {
        return developerRepository.existsByName(name)
    }

    @Transactional
    override fun followDeveloper(fromUserId: String, toUserId: String) {
        if (fromUserId == toUserId) throw BusinessException(Error.DEVELOPER_SELF_FOLLOW)
        var fromDeveloper = findDeveloperByUserId(fromUserId) ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        var toDeveloper = findDeveloperByUserId(toUserId) ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        var follow = followRepository.findAllByFromDeveloperAndToDeveloper(fromDeveloper, toDeveloper)

        // 이미 팔로우 돼있는지 확인
        if (follow == null) {
            follow = Follow(null)

            follow.fromDeveloper = fromDeveloper
            follow.toDeveloper = toDeveloper

            followRepository.save(follow)
        } else {
            followRepository.delete(follow)
        }
    }

    override fun findFollowersByUserId(userId: String): Int {
        var developer: Developer? = developerRepository.findByUserId(userId)

        // 사용자가 null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        return followRepository.countByToDeveloper(developer)
    }

    override fun existsFollowByUserId(fromUserId: String?, toUserId: String?): Boolean {

        fromUserId ?: return false
        toUserId ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        return followRepository.existsByFromDeveloperUserIdAndToDeveloperUserId(fromUserId, toUserId)
    }
}