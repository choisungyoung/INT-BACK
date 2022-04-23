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

    val withdrawalDeveloperEmail: String = "withdrawalDeveloper@notworking.com";

    @Transactional
    override fun saveDeveloper(developer: Developer) {
        developer.let {
            // 아이디 중복 체크
            if (developerRepository.existsByEmail(developer.email)) {
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

    override fun findDeveloperByEmail(email: String): Developer? {
        return developerRepository.findByEmail(email)
    }

    @Transactional
    override fun updateDeveloper(newDeveloper: Developer): Developer? {
        var developer: Developer? = newDeveloper.email?.let { developerRepository.findByEmail(it) }
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
    override fun updatePasswordDeveloper(email: String, password: String) {
        var developer: Developer? = developerRepository.findByEmail(email)

        // null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        developer.pwd = passwordEncoder.encode(password)
    }

    @Transactional
    override fun deleteDeveloper(email: String) {
        var developer: Developer? = developerRepository.findByEmail(email)
        var withdrawalDeveloper: Developer? = developerRepository.findByEmail(withdrawalDeveloperEmail)
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

    override fun existsDeveloperByEmail(email: String): Boolean {
        return developerRepository.existsByEmail(email)
    }

    override fun existsDeveloperByName(name: String): Boolean {
        return developerRepository.existsByName(name)
    }

    @Transactional
    override fun followDeveloper(fromEmail: String, toEmail: String) {
        if (fromEmail == toEmail) throw BusinessException(Error.DEVELOPER_SELF_FOLLOW)
        var fromDeveloper = findDeveloperByEmail(fromEmail) ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        var toDeveloper = findDeveloperByEmail(toEmail) ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

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

    override fun findFollowersByEmail(email: String): Int {
        var developer: Developer? = developerRepository.findByEmail(email)

        // 사용자가 null일 경우 예외처리
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        return followRepository.countByToDeveloper(developer)
    }

    override fun existsFollowByEmail(fromEmail: String?, toEmail: String?): Boolean {

        fromEmail ?: return false
        toEmail ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        return followRepository.existsByFromDeveloperEmailAndToDeveloperEmail(fromEmail, toEmail)
    }

    /*
    @Transactional
    override fun checkAuthNumByEmail(email: String, authNum: Int): Boolean {

        var developer = findDeveloperByEmail(email)
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        if (developer.authNum == 0) {
            throw BusinessException(Error.DEVELOPER_HAS_NOT_AUTH_NUM)
        }

        if (developer.authNum == authNum) {
            return true
        }

        return false
    }
     */
}